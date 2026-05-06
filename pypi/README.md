# pythonbundle

**pythonbundle** is a versatile and lightweight collection of Python utility scripts and classes designed to accelerate development. It provides simplified interfaces for common programming tasks ranging from data structure manipulation, cryptography, string validation, and type conversions, to handling SQLite databases, Excel files, and sorting algorithms.

---

## 📦 Features

`pythonbundle` contains several distinct modules to help keep your codebase clean and readable:

### 🛡️ Validation (`Check`)
A robust validation class to simplify standard string and format checks.
- Email formatting and customizable domain validation.
- Validates Philippine mobile numbers (+639 / 09).
- Check for spaces, symbols, and pure numerical strings.

### 🔐 Cryptography (`Cipher`)
A basic cryptography class to implement classic cipher techniques.
- Transposition Cipher
- Giovanni Cipher
- Keyword Cipher
- Caesar Cipher

### 🔄 Data Conversion (`Convert`)
Effortless type casting and data encoding.
- Hex, Binary, and Base64 encoding/decoding.
- String reversal and byte-array conversions.
- Quick casting for Int, Float, Double, and Long.

### 📚 Data Structures
Custom implementations for improved data manipulation.
- **`Dictionarily`**: An enhanced Dictionary object with built-in sorting (alphabetical and numerical-first).
- **`Memory`**: A clean, object-oriented list/array wrapper to handle storage, indexing, and removal.
- **`Stackily`**: A classic Stack implementation (`push`, `pop`, `peek`, `is_empty`, `size`).
- **`Node`**: A lightweight binary tree node implementation.

### 🗄️ Database Management (`EasySQL`)
A simplified wrapper around Python's built-in `sqlite3`.
- Create tables with ease by passing lists of dictionaries.
- Insert, delete, and clear records directly via Python dictionaries.
- Fetch and print table values seamlessly.

### 📊 Excel Operations
A wrapper for `openpyxl` allowing for extremely fast Excel file data manipulation.
- Read and write to specific columns across single or multiple sheets.
- Skip header rows easily using `skip_rows`.
- Zero-hassle reading/writing to entire column letters (e.g., Column 'A').

### 🗂️ File Handling (`SimpleFileHandler`)
Static methods to rapidly `read()`, `write()`, and `append()` to text files using `utf-8` encoding.

### 🧹 Sorting Algorithms
A massive suite of sorting algorithms available as quick plug-and-play functions.
- Quick Sort, Merge Sort, Heap Sort, Selection Sort, Insertion Sort, Bubble Sort.
- Advanced/Niche Sorts: Tim Sort, Intro Sort, Cocktail Shaker Sort, Shell Sort, Pigeonhole Sort, Bead Sort, and even Bogo Sort!

---

## 🚀 Installation

```bash
pip install pythonbundle
```

### Dependencies
The package largely uses Python's standard library (e.g., `sqlite3`, `math`, `re`, `base64`). However, the Excel operations module requires:
- `openpyxl`
- `unidecode`

---

## 💻 Quick Usage Examples

### 1. Simple SQLite Database Queries (`EasySQL`)
```python
from pythonbundle import EasySQL

db = EasySQL()

# Create a database table
db.create_table("users_db", "employees",[{"first_name": "text"}, {"last_name": "text"}])

# Insert data
db.insert_to_table("users_db", "employees", [{"first_name": "John"}, {"last_name": "Doe"}])

# Fetch values
records = db.get_table_values("users_db", "employees")
print(records)
```

### 2. Validating Phone Numbers & Emails (`Check`)
```python
from pythonbundle import Check

# Validate Philippine Phone Numbers
is_valid = Check.is_a_valid_philippine_mobile_number("+639123456789")
print(is_valid)  # True

# Validate Emails with strict domain rules
Check.Email.add_valid_domain_name("gmail")
Check.Email.add_valid_domain_extension("com")
print(Check.Email.is_valid("user@gmail.com"))  # True
```

### 3. File Handling (`SimpleFileHandler`)
```python
from pythonbundle import SimpleFileHandler

# Write, Append, and Read
SimpleFileHandler.write("log.txt", "Process started.\n")
SimpleFileHandler.append("log.txt", "Process finished.\n")

print(SimpleFileHandler.read("log.txt"))
```

### 4. Text Encryption (`Cipher`)
```python
from pythonbundle import Cipher

encrypted = Cipher.caesar_cipher("HELLO WORLD", shift=3)
print(encrypted)  # KHOOR ZRUOG
```

### 5. Sorting Array Data
```python
from pythonbundle import quicksort, merge_sort

array = [5, 2, 9, 1, 5, 6]
print(quicksort(array)) #[1, 2, 5, 5, 6, 9]
```

---

## 🤝 Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change. 

## 📝 License
[MIT](https://choosealicense.com/licenses/mit/)