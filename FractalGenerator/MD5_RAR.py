import hashlib
def md5sum(filename, blocksize=65536):
    hash = hashlib.md5()
    with open(filename, "r+b") as f:
        for block in iter(lambda: f.read(blocksize), ""):
            hash.update(block)
    return hash.hexdigest()
md=md5sum(".\\dist\\Fractal_Generator_Reborn.jar")
print(md)
f=open("Fractal_Generator_Reborn.jar.MD5","w")
f.write(md)
f.close()
raw_input()

