import itertools
import math
from typing import List

import cv2
import numpy as np
from scipy.spatial.distance import cosine
from sklearn.metrics import f1_score
from tqdm.auto import tqdm


def load_imgs(paths: str) -> np.ndarray:
    mean = [0.485, 0.456, 0.406]
    std = [0.229, 0.224, 0.225]
    imgs = []
    for path in paths:
        img = cv2.resize(cv2.imread(path), (224, 224))
        imgs.append(img / img.max())
    imgs = np.array(imgs).astype(np.float32)
    imgs = np.transpose(imgs, axes=(0, -1, 1, 2))
    for i in range(3):
        imgs[:, i] = (imgs[:, i] - mean[i]) / std[i]
    return imgs


def sim_mat(key_imgs: List[np.ndarray], imgs: np.ndarray) -> np.ndarray:
    scores = []
    for img in imgs:
        cls_score = []
        for key_img in key_imgs:
            cls_score.append(cosine(key_img, img))
        scores.append(np.argmin(cls_score))
    return np.array(scores)


def test_score(pred: np.ndarray, y: np.ndarray) -> List[float]:
    y = np.array(y)
    scores = []
    unq, count = np.unique(y, return_counts=True)
    idx = np.cumsum(count)

    weights = 1 / (count - 1)
    weights = weights / sum(weights)
    total = math.prod(count)
    for i, keys in enumerate(
            tqdm(itertools.product(*np.split(pred, idx[:-1])), total=total)):
        rest_imgs = [np.delete(pred[y == cls], i % c, axis=0) for cls, c in
                     enumerate(count)]
        rest_imgs = np.concatenate(rest_imgs)
        sim_score = sim_mat(keys, rest_imgs)
        y_score = np.concatenate(
            [np.delete(y[y == cls], i % c) for cls, c in enumerate(count)])
        score = f1_score(y_true=y_score, y_pred=sim_score, average="weighted")
        scores.append(score)
    return scores
