import os
from glob import glob

import numpy as np
import torch as T

from src import models
from src.utils import load_images, test_score

if __name__ == "__main__":
    model_name = "BiT-M-R50x1"
    serialize = False
    models = [
        models.KNOWN_MODELS[model_name](head_size=1000, zero_head=True)
        # torchvision.models.resnet18(pretrained=True), # 0.717
        # torchvision.models.resnet152(pretrained=True), # 0.677
        # torchvision.models.googlenet(pretrained=True), # 0.856
        # torchvision.models.resnext50_32x4d(pretrained=True), # 0.732
        # torchvision.models.resnext101_32x8d(pretrained=True), # 0.636
        # torchvision.models.wide_resnet50_2(pretrained=True), # 0.858
        # torchvision.models.wide_resnet101_2(pretrained=True), # 0.729
        # torchvision.models.mnasnet0_5(pretrained=True), # 0.668
        # torchvision.models.mnasnet1_0(pretrained=True) # 0.732
    ]
    for model in models:
        # model = T.nn.Sequential(model, T.nn.Softmax(-1))
        # ------- serialize -------
        if serialize:
            example = T.rand(1, 3, 224, 224)
            traced_script_module = T.jit.trace(model, example)
            traced_script_module.save("output_models/flowers.pt")
        # ------- serialize -------
        print(model)
        model.eval()
        paths = ["rose", "tulip", "sunflower"]
        imgs = np.concatenate(
            [load_images(glob(os.path.join("imgs", path, "*.jpg"))) for path in paths]
        )
        cls = {i: len(os.listdir(os.path.join("imgs", path))) for i, path in
               enumerate(paths)}
        assert len(imgs) == sum(cls.values())
        y = np.array([c for k, v in cls.items() for c in [k] * v])
        with T.no_grad():
            pred = model(T.tensor(imgs)).numpy().squeeze()
        scores = test_score(pred, y)
        print(f"Scores: {scores}")
        print(f"Mean: {np.mean(scores):.3f}")
