import requests
import re

url = "https://quranenc.com/api/v1/translation/sura/kannada_hamza/1"

try:
    response = requests.get(url)
    if response.status_code == 200:
        data = response.json()
        for item in data["result"]:
            translation = item["translation"]
            # Replace footnotes
            translation = re.sub(r'\[(\d+)\]', lambda x: item["footnotes"][int(x.group(1))-1], translation)
            # Remove additional footnotes
            translation = re.sub(r'\[\d+\].*?\n', '', translation)
            print("ID:", item["id"])
            print("Sura:", item["sura"])
            print("Aya:", item["aya"])
            print("Translation:", translation)
            print()
    else:
        print("Error: Unable to fetch data. Status code:", response.status_code)
except requests.exceptions.RequestException as e:
    print("Error: ", e)
