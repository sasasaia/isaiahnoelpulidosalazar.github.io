class Memory:
    def __init__(self):
        self.storage = []

    def add(self, data):
        self.storage.append(data)

    def contains(self, data):
        return data in self.storage

    def remove(self, data):
        self.storage.remove(data)

    def remove_at(self, index):
        self.storage.pop(index)

    def get(self, index):
        return self.storage[index]

    def count(self):
        return len(self.storage)

    def clear(self):
        self.storage = []