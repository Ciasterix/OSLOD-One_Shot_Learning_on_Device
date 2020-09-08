import os
from glob import glob

import numpy as np
import torch

from Learner import face_learner
from config import get_config
from src.utils import load_images, test_score

if __name__ == "__main__":
    conf = get_config(False)
    serialize = False

    learner = face_learner(conf, True)
    learner.threshold = 1.54
    learner.load_state(conf, 'cpu.pth', True)
    learner.model.eval()

    if serialize:
    # ------- serialize -------
        example = torch.rand(1, 3, 112, 112)
        traced_script_module = torch.jit.trace(learner.model, example)
        traced_script_module.save("output_models/face_model.pt")
    # ------- serialize -------

    newmodel = learner.model
    paths = ["gosling", "reynolds", "jackman", "spears", "cena"]
    imgs = np.concatenate(
        [load_images(glob(os.path.join("imgs", path, "*.jpg")), size=112) for path in
         paths]
    )
    cls = {i: len(os.listdir(os.path.join("imgs", path))) for i, path in
           enumerate(paths)}
    assert len(imgs) == sum(cls.values())
    y = np.array([c for k, v in cls.items() for c in [k] * v])

    with torch.no_grad():
        tmp = torch.tensor(imgs)
        pred = newmodel(torch.tensor(imgs)).numpy().squeeze()
    scores = test_score(pred, y)
    print(f"Scores: {scores}")
    print(f"Mean: {np.mean(scores):.3f}")
