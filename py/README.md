# pythonbundle
A comprehensive collection of Python utility scripts covering data structures, cryptography, database management, sorting algorithms, and file processing.

# Available methods
- [builder_for_flask_jsonify.py](#builder_for_flask_jsonify) - Flask helper for JSON responses.
- [check.py](#check) - String validation and character checking.
- [cipher.py](#cipher) - Cryptographic algorithms (Caesar, Keyword, Transposition).
- [convert.py](#convert) - Base64 encoding and decoding.
- [dictionarily.py](#dictionarily) - Enhanced dictionary management.
- [easy_sql.py](#easy_sql) - Simplified SQLite3 wrapper.
- [excellent_reader.py](#excellent_reader) - Excel file reader using openpyxl.
- [memory.py](#memory) - Dynamic list-based storage.
- [node.py](#node) - Basic tree node class.
- [sort.py](#sort) - Extensive collection of sorting algorithms.
- [stackily.py](#stackily) - Stack data structure implementation.

## builder_for_flask_jsonify
Simple utility to wrap messages in a dictionary for Flask's jsonify function.
### Key Constant
```python
JSON_RESPONSE_TITLE = "response_data"
```
### Function
```python
bake(message) # Returns {"response_data": message}
```
## check
Contains functions to check for the presence of specific character types in strings.
```python
has_numbers(string)
has_numbers2(string)

has_spaces(string)
has_spaces2(string)

has_symbols(string)
has_symbols2(string)
```
## cipher
Classic cryptography methods for text manipulation.
### Caesar Cipher
Shifts characters based on an integer.
### Keyword Cipher
Uses a keyword to generate a substitution alphabet.
### Transposition Cipher
Splits text into even and odd indices and joins them.
## convert
Quick conversion between UTF-8 strings and Base64 format.
```python
string_to_base64(string)
base64_to_string(string)
```
## dictionarily
A wrapper for Python dictionaries with custom sorting capabilities.

```sort()```: Standard alphabetical sort.

```sort_numbers_first()```: Logic to prioritize numeric keys over string keys.
## easy_sql
A class-based wrapper to interact with SQLite databases without writing raw SQL for common tasks.
### How to use
```python
# Import the class
from easy_sql import EasySQL

# Create an instance of the class
a = EasySQL()

# Define the columns for the table
columns = [{"fname": "text"}, {"lname": "text"}]

# Create specified table
a.create_table("sample", "sampletable", columns)

# Define the values to be inserted
values = [{"fname": "Isaiah"}, {"lname": "Salazar"}]
values1 = [{"fname": "Saia"}, {"lname": "Razalas"}]

# Insert the values into specified table
a.insert_to_table("sample", "sampletable", values)
a.insert_to_table("sample", "sampletable", values1)

# Print the values in specified table
print(a.get_table_values("sample", "sampletable"))

# Delete a column-value pair from a specified table
a.delete_from_table("sample", "sampletable", {"fname": "Saia"})
```
## excellent_reader
Reads the first column of Excel (```.xlsx```) spreadsheets.

```get_first_column_from_sheet_index()```: Targets a specific sheet.

```get_first_column_from_all_sheets()```: Iterates through the entire workbook.
## memory
A high-level wrapper for list operations including adding, removing, and counting elements.
## node
The fundamental building block for tree-based structures.
```python
class Node:
    def __init__(self, val):
        self.l, self.r, self.v = None, None, val
```
## sort
A massive library of sorting algorithms, ranging from basic to complex:
### Basic
```bubble_sort, selection_sort, insertion_sort```
### Efficient
```quicksort, merge_sort, heapsort, timsort```
### Niche
```shellsort, counting_sort, pigeonhole_sort, tree_sort```
### Experimental/Fun
```bogosort, bead_sort, cocktail_shaker_sort```
## stackily
A standard implementation of a LIFO (Last-In-First-Out) Stack.
### Methods
```push, pop, peek, is_empty, size```