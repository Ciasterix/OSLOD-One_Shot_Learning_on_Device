from pathlib import Path

import torch
from easydict import EasyDict as edict
from torchvision import transforms as trans


def get_config(training = True):
    conf = edict()
    conf.work_path = Path('/home/krigaree/studies/mgr_sem1/tp/tp_p/OSLOD-One_Shot_Learning_on_Device/models/faces/')
    conf.model_path = conf.work_path/'weights'
    conf.log_path = conf.work_path/'log'
    conf.save_path = conf.work_path/'weights'
    conf.input_size = [112, 112]
    conf.embedding_size = 512
    conf.use_mobilfacenet = False
    conf.net_depth = 50
    conf.drop_ratio = 0.6
    conf.net_mode = 'ir_se' # or 'ir'
    conf.device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
    conf.test_transform = trans.Compose([
                    trans.ToTensor(),
                    trans.Normalize([0.5, 0.5, 0.5], [0.5, 0.5, 0.5])
                ])
    conf.data_mode = 'emore'
    conf.threshold = 1.5
    conf.face_limit = 10
    #when inference, at maximum detect 10 faces in one image,
    conf.min_face_size = 35
    # the larger this value, the faster deduction, comes with tradeoff in small faces
    return conf
