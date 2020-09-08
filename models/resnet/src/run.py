import os
from glob import glob

import numpy as np
import torch
import torchvision

from src.utils import load_images, test_score

if __name__ == "__main__":
    model = torchvision.models.resnet18(pretrained=True)  # 0.859, 0.906
    # model = torchvision.models.resnet152(pretrained=True)  # 0.964, 0.920
    # model = torchvision.models.googlenet(pretrained=True)  # 0.937, 0.960
    # model = torchvision.models.resnext50_32x4d(pretrained=True)  # 0.839, 0.907
    # model = torchvision.models.resnext101_32x8d(pretrained=True)  # 0.955, 0.973
    # model = torchvision.models.wide_resnet50_2(pretrained=True)  # 0.744, 0.854
    # model = torchvision.models.wide_resnet101_2(pretrained=True)  # 0.912, 0.944
    # model = torchvision.models.mnasnet0_5(pretrained=True)  # 0.783, 0.841
    # model = torchvision.models.mnasnet1_0(pretrained=True)  # 0.789, 0.868
    model.eval()
    # example = torch.rand(4, 3, 224, 224)
    # newmodel = torch.nn.Sequential(*(list(model.children())[:-1]),
    #                                torch.nn.Softmax(1))
    # newmodel = torch.nn.Sequential(*(list(model.children())[:-1]))
    # newmodel = torch.nn.Sequential(model, torch.nn.Softmax(-1))
    newmodel = model

    # All -> 0.922
    # paths = ["candle", "charger", "gloves", "keys", "phone", "tv_remote"]
    # paths += ["cats", "dogs"]
    paths = ["cats", "dogs"]
    imgs = np.concatenate(
        [load_images(glob(os.path.join("imgs", path, "*.jpg"))) for path in paths]
    )
    cls = {i: len(os.listdir(os.path.join("imgs", path))) for i, path in
           enumerate(paths)}
    assert len(imgs) == sum(cls.values())
    y = np.array([c for k, v in cls.items() for c in [k] * v])

    with torch.no_grad():
        pred = newmodel(torch.tensor(imgs)).numpy().squeeze()
    scores = test_score(pred, y)
    print(f"Mean: {np.mean(scores):.3f}")
