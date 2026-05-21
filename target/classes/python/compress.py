from pathlib import Path
import fitz  # PyMuPDF
from PIL import Image
import io
import sys
import shutil
import os

input_pdf = Path(sys.argv[1])
output_pdf = Path(sys.argv[2])
target_kb = int(sys.argv[3])

# 📌 Tamaño original
original_size_kb = os.path.getsize(input_pdf) / 1024

print(f"Original size: {original_size_kb:.2f} KB")
print(f"Target size: {target_kb} KB")

# 🚀 1. SI NO HACE FALTA COMPRIMIR → DEVUELVE ORIGINAL
if target_kb >= original_size_kb:
    print("No compression needed, copying original PDF")

    shutil.copy(input_pdf, output_pdf)

    print(f"Final size: {original_size_kb:.2f} KB")
    sys.exit(0)


# 🚀 2. COMPRIMIR SOLO SI ES NECESARIO
doc = fitz.open(str(input_pdf))

images = []
for page in doc:
    pix = page.get_pixmap(matrix=fitz.Matrix(1.0, 1.0), alpha=False)
    img = Image.frombytes("RGB", [pix.width, pix.height], pix.samples)
    images.append(img)

quality = 50
best_pdf = None
best_diff = float("inf")

# 🔁 búsqueda progresiva
while quality >= 5:

    buffer = io.BytesIO()

    rgb_images = [img.convert("RGB") for img in images]

    rgb_images[0].save(
        buffer,
        format="PDF",
        save_all=True,
        append_images=rgb_images[1:],
        quality=quality,
        optimize=True
    )

    size_kb = len(buffer.getvalue()) / 1024
    diff = abs(target_kb - size_kb)

    print(f"Quality {quality} → {size_kb:.2f} KB")

    # guardamos el mejor resultado (más cercano al target)
    if diff < best_diff:
        best_diff = diff
        best_pdf = buffer.getvalue()

    # si ya estamos por debajo del target, seguimos bajando pero buscando mejor match
    quality -= 5

# 💾 guardar el mejor resultado encontrado
with open(output_pdf, "wb") as f:
    f.write(best_pdf)

final_size = os.path.getsize(output_pdf) / 1024

print(f"Final compressed size: {final_size:.2f} KB")