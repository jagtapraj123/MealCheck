# Meal Check

## Dataset

We use [Food.com recipes dataset](https://www.kaggle.com/datasets/shuyangli94/food-com-recipes-and-user-interactions) dataset from Kaggle.

The dataset can be downloaded using shell script in `dataset` directory.

```sh
cd dataset
sh get_dataset.sh
```

## Installation

1. Create virtual environment.
   - **Using Conda**

        ```sh
        conda create --name meal_check python=3.9
        conda activate meal_check
        ```

        Anything above python=3.7+ would be ok.
   - **Using virtualenv**

        ```sh
        virtualenv venv_meal_check
        source venv_meal_check/bin/activate
        ```

2. Install Python Requirements.

    ```sh
    pip install -r requirements.txt
    ```

## Other Requirements

- [MongoDB](https://www.mongodb.com/try/download/community)

## Training Models

**This part can be skipped as trained models are already provided.**

The trained models can be found here: [Google Drive](https://drive.google.com/file/d/1iq0OHlYfnzNsQkuqVlt2I8hT-y77gAg0/view?usp=sharing)

*Requirements: Install Python dependancies mentioned in Installation Section.*


The training code is present in `training` directory.

```bash
cd training
```

and run

```bash
python train_recipe.py
```

## Running Backend Instance

The backend server can be run as follows:

```bash
cd api
python app.py
```

This will host the app on `http://localhost:8000`.
