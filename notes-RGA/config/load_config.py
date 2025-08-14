import os
import json
import getpass

def load_config(keyname: str) -> object:
    # 获取当前脚本文件所在的绝对路径，并定位 Keys.json 到该路径下
    current_dir = os.path.dirname(os.path.abspath(__file__))
    file_name = os.path.join(current_dir, "config.json")

    with open(file_name, "r") as file:
        config = json.load(file)

    return config[keyname]

