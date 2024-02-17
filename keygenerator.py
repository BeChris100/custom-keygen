import random
import threading


# Adjusted constraints for the updated rules
divisors = [27, 16, 21]
lengths = [9, 4, 7]

# Function to generate a random valid hexadecimal number that meets the new criteria
def generate_valid_hex_number(length, divisor):
    valid = False
    while not valid:
        # Generate a random hexadecimal number
        number = ''.join(random.choice('0123456789ABCDEF') for _ in range(length))
        # Check if it contains at least one digit and one letter, and is divisible accordingly
        if any(char.isdigit() for char in number) and any(char.isalpha() for char in number):
            l = int(number, 16)
            if l % divisor == 0:
                valid = True
    return number

def mk_key():
    # Generate complex parts for the key under the new constraints
    updated_complex_parts = [
        generate_valid_hex_number(length, divisor) for length, divisor in zip(lengths, divisors)
    ]

    print('1983-' + '-'.join(updated_complex_parts))

for _ in range(500):
    x = threading.Thread(target=mk_key)
    x.start()
