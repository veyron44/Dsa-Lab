# Dsa-Lab
  GPS Retracer 
A command-driven program that reconstructs a hiker’s return path by reversing logged GPS movements using a stack-based approach.

  Running the Program 
1. Save the file as GPSRetracer.py
2. Run the program using: python GPSRetracer.py
   
  Available Commands 
LOG <action>
Stores a movement action in the stack.
•	LOG FWD <distance> → Logs forward movement
•	LOG LEFT → Logs left turn
•	LOG RIGHT → Logs right turn

 CALCULATE_RETURN – Reverses and inverts all logged movements and displays the return path. 
EXIT – Stops program execution.

  Reverse Logic Applied
LEFT → RIGHT 
RIGHT → LEFT 
FWD X → FWD X

  Example 
Input: 
LOG FWD 100 
LOG LEFT
LOG FWD 50 
CALCULATE_RETURN
 
Output: 
1. FWD 50
2. RIGHT 
3. FWD 100
   
  Implementation Notes
- Uses a stack (LIFO) to reverse actions
- Most recent action is processed first
- No coordinate or direction tracking is performed
- Stack is cleared after generating return instructions
