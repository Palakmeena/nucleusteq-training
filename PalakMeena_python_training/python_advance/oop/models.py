class Student:
    """
    Q40. Create a Student class with attributes and display details.
    """

    def __init__(self, name: str, age: int, course: str) -> None:
        self.name = name
        self.age = age
        self.course = course

    def display_details(self) -> None:
        print("Name:", self.name)
        print("Age:", self.age)
        print("Course:", self.course)



class Car:
    """
    Q41. Create a Car class with a constructor.
    """

    def __init__(self, brand: str, model: str) -> None:
        self.brand = brand
        self.model = model

    def show_car(self) -> None:
        print("Brand:", self.brand)
        print("Model:", self.model)



class Person:
    """
    Base class Person.
    """

    def __init__(self, name: str) -> None:
        self.name = name


class Employee(Person):
    """
    Q42. Inheritance example using Person and Employee.
    """

    def __init__(self, name: str, salary: int) -> None:
        super().__init__(name)
        self.salary = salary

    def show_employee(self) -> None:
        print("Name:", self.name)
        print("Salary:", self.salary)



class Bank:
    """
    Q43. Encapsulation using private variables.
    """

    def __init__(self, balance: int) -> None:
        self.__balance = balance

    def deposit(self, amount: int) -> None:
        self.__balance += amount

    def get_balance(self) -> int:
        return self.__balance


class Dog:
    """
    Q44. Polymorphism example.
    """

    def sound(self) -> None:
        print("Dog barks")


class Cat:
    def sound(self) -> None:
        print("Cat meows")



if __name__ == "__main__":
    student = Student("Palak", 21, "Python")
    student.display_details()

    print()

    car = Car("Toyota", "Innova")
    car.show_car()

    print()

    emp = Employee("John", 50000)
    emp.show_employee()

    print()

    bank = Bank(1000)
    bank.deposit(500)
    print("Balance:", bank.get_balance())

    print()

    animals = [Dog(), Cat()]
    for animal in animals:
        animal.sound()