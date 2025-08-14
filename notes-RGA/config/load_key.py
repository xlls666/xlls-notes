import os
import json
import getpass

def load_key(keyname: str) -> object:
    # 获取当前脚本文件所在的绝对路径，并定位 Keys.json 到该路径下
    current_dir = os.path.dirname(os.path.abspath(__file__))
    file_name = os.path.join(current_dir, "Keys.json")
    if os.path.exists(file_name):
        with open(file_name, "r") as file:
            Key = json.load(file)

        if keyname in Key and Key[keyname]:
            return Key[keyname]
        else:
            keyval = getpass.getpass("配置文件中没有相应就，请输入对应配置信息:").strip()
            Key[keyname] = keyval
            with open(file_name, "w") as file:
                json.dump(Key, file, indent=4)
            return keyval
    else:
        keyval = getpass.getpass("配置文件中没有相应就，请输入对应配置信息:").strip()
        Key = {
            keyname: keyval
        }
        with open(file_name, "w") as file:
            json.dump(Key, file, indent=4)
        return keyval

