import itertools
import math
from typing import List, Any

import cv2
import numpy as np
from scipy.spatial.distance import cosine
from sklearn.metrics import f1_score
from tqdm.auto import tqdm


def transform_func(images: List[np.ndarray]) -> np.ndarray:
    mean = [0.485, 0.456, 0.406]
    std = [0.229, 0.224, 0.225]
    transformed_images = np.array(images).astype(np.float32)
    transformed_images = np.transpose(transformed_images,
                                      axes=(0, -1, 1, 2))  # type: ignore
    for i in range(3):
        transformed_images[:, i] = (transformed_images[:, i] - mean[i]) / std[i]
    return transformed_images


def load_images(paths: str) -> np.ndarray:
    images = []
    for path in paths:
        img = cv2.resize(cv2.imread(path), (224, 224))
        images.append(img / img.max())
    transformed_images = transform_func(images)
    return transformed_images


def sim_mat(key_images: List[np.ndarray], images: np.ndarray) -> np.ndarray:
    scores = []
    for img in images:
        cls_score = []
        for key_img in key_images:
            cls_score.append(cosine(key_img, img))
        scores.append(np.argmin(cls_score))
    return np.array(scores)


def test_score(pred: np.ndarray, y: np.ndarray) -> List[float]:
    scores = []
    unq, count = np.unique(y, return_counts=True)
    idx = np.cumsum(count)  # type: ignore
    total = math.prod(count)
    for i, keys in enumerate(
            tqdm(itertools.product(*np.split(pred, idx[:-1])), total=total)):
        rest_images = np.concatenate(
            [np.delete(pred[y == cls], i % c, axis=0) for cls, c in
             enumerate(count)]
        )
        sim_score = sim_mat(keys, rest_images)
        y_score = np.concatenate(
            [np.delete(y[y == cls], i % c) for cls, c in enumerate(count)])
        score = f1_score(y_true=y_score, y_pred=sim_score, average="weighted")
        scores.append(score)
    return scores
