import torch
from matplotlib import pyplot as plt

from model import Backbone, MobileFaceNet, l2_norm

plt.switch_backend('agg')
from torchvision import transforms as trans


class face_learner(object):
    def __init__(self, conf, inference=False):
        print(conf)
        if conf.use_mobilfacenet:
            self.model = MobileFaceNet(conf.embedding_size).to(conf.device)
            print('MobileFaceNet model generated')
        else:
            self.model = Backbone(conf.net_depth, conf.drop_ratio,
                                  conf.net_mode).to(conf.device)
            print('{}_{} model opened'.format(conf.net_mode, conf.net_depth))

        self.threshold = conf.threshold

    def load_state(self, conf, fixed_str, from_save_folder=False):
        if from_save_folder:
            save_path = conf.save_path
        else:
            save_path = conf.model_path
        self.model.load_state_dict(
            torch.load(save_path / 'model_{}'.format(fixed_str)))

    def save_state(self, conf, to_save_folder=False):
        if to_save_folder:
            save_path = conf.save_path
        else:
            save_path = conf.model_path
        torch.save(self.model.state_dict(), save_path / ('model_cpu.pth'))

    def infer(self, conf, faces, target_embs, tta=False):
        '''
        faces : list of PIL Image
        target_embs : [n, 512] computed embeddings of faces in facebank
        names : recorded names of faces in facebank
        tta : test time augmentation (hfilp, that's all)
        '''
        embs = []
        for img in faces:
            if tta:
                mirror = trans.functional.hflip(img)
                emb = self.model(
                    conf.test_transform(img).to(conf.device).unsqueeze(0))
                emb_mirror = self.model(
                    conf.test_transform(mirror).to(conf.device).unsqueeze(0))
                embs.append(l2_norm(emb + emb_mirror))
            else:
                embs.append(self.model(
                    conf.test_transform(img).to(conf.device).unsqueeze(0)))
        source_embs = torch.cat(embs)

        diff = source_embs.unsqueeze(-1) - target_embs.transpose(1,
                                                                 0).unsqueeze(0)
        dist = torch.sum(torch.pow(diff, 2), dim=1)
        minimum, min_idx = torch.min(dist, dim=1)
        min_idx[minimum > self.threshold] = -1  # if no match, set idx to -1
        return min_idx, minimum
