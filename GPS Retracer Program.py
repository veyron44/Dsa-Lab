# GPS Retracer Program
# Uses a stack to store actions and calculates the reverse path

class GPSRetracer:
    def __init__(self):
        self.stack = []

    def log_action(self, action):
        # Store action in stack
        self.stack.append(action)

    def invert_action(self, action):
        parts = action.split()

        if parts[0] == "LEFT":
            return "RIGHT"
        elif parts[0] == "RIGHT":
            return "LEFT"
        elif parts[0] == "FWD":
            return action  # Forward distance stays the same
        else:
            return action

    def calculate_return(self):
        reversed_steps = []
        step_number = 1

        # Read stack in reverse order
        while self.stack:
            action = self.stack.pop()
            inverted = self.invert_action(action)
            reversed_steps.append(f"{step_number}. {inverted}")
            step_number += 1

        return reversed_steps


def main():
    gps = GPSRetracer()

    print("GPS Retracer CLI")
    print("Commands:")
    print("LOG <FWD distance | LEFT | RIGHT>")
    print("CALCULATE_RETURN")
    print("Type EXIT to quit\n")

    while True:
        command = input("> ").strip().upper()

        if command == "EXIT":
            break

        elif command.startswith("LOG"):
            parts = command.split(maxsplit=1)
            if len(parts) == 2:
                gps.log_action(parts[1])
            else:
                print("Invalid LOG command")

        elif command == "CALCULATE_RETURN":
            result = gps.calculate_return()
            if not result:
                print("No actions logged.")
            else:
                for line in result:
                    print(line)

        else:
            print("Unknown command")


if __name__ == "__main__":
    main()
