import java.util.Scanner;

public class AdvancedTextEditor {
    static class CharNode {
        char ch;
        CharNode prev, next;
        CharNode(char ch) { this.ch = ch; }
    }

    enum ActionType { INSERT, DELETE }

    static class Action {
        ActionType type;
        char ch;
        CharNode node;
        Action(ActionType type, char ch, CharNode node) {
            this.type = type;
            this.ch = ch;
            this.node = node;
        }
    }

    static class ActionNode {
        Action action;
        ActionNode prev, next;
        ActionNode(Action action) { this.action = action; }
    }

    static class ActionBatch {
        ActionType type;
        ActionNode head, tail;
        long lastTime;
        ActionBatch next;

        ActionBatch(ActionType type) {
            this.type = type;
            this.lastTime = System.currentTimeMillis();
        }

        void addAction(Action action) {
            ActionNode node = new ActionNode(action);
            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                node.prev = tail;
                tail = node;
            }
            lastTime = System.currentTimeMillis();
        }
    }

    static class Stack {
        ActionBatch top;
        boolean isEmpty() { return top == null; }
        void push(ActionBatch batch) { batch.next = top; top = batch; }
        ActionBatch pop() {
            if (isEmpty()) return null;
            ActionBatch temp = top;
            top = top.next;
            return temp;
        }
        ActionBatch peek() { return top; }
        void clear() { top = null; }
    }

    private CharNode head, tail, cursor;
    private Stack undoStack = new Stack();
    private Stack redoStack = new Stack();
    private static final long BATCH_TIME = 2000;

    public AdvancedTextEditor() {
        head = new CharNode('\0');
        tail = new CharNode('\0');
        head.next = tail;
        tail.prev = head;
        cursor = head;
    }

    public void write(char ch) {
        CharNode newNode = new CharNode(ch);
        newNode.prev = cursor;
        newNode.next = cursor.next;
        cursor.next.prev = newNode;
        cursor.next = newNode;
        cursor = newNode;
        recordAction(new Action(ActionType.INSERT, ch, newNode));
        redoStack.clear();
    }

    public void delete() {
        if (cursor == head) return;
        CharNode target = cursor;
        target.prev.next = target.next;
        target.next.prev = target.prev;
        cursor = target.prev;
        recordAction(new Action(ActionType.DELETE, target.ch, target));
        redoStack.clear();
    }

    public void moveLeft() {
        if (cursor != head) cursor = cursor.prev;
        breakBatch();
    }

    public void moveRight() {
        if (cursor.next != tail) cursor = cursor.next;
        breakBatch();
    }

    public void undo() {
    ActionBatch batch = undoStack.pop();
    if (batch == null) return;

    ActionNode current = batch.tail;

    if (batch.type == ActionType.INSERT) {
        while (current != null) {
            Action a = current.action;
            a.node.prev.next = a.node.next;
            a.node.next.prev = a.node.prev;
            cursor = a.node.prev;
            current = current.prev;
        }
    } else {
        while (current != null) {
            Action a = current.action;
            a.node.prev.next = a.node;
            a.node.next.prev = a.node;
            cursor = a.node;
            current = current.prev;
        }
    }
    redoStack.push(batch);
}

    public void redo() {
    ActionBatch batch = redoStack.pop();
    if (batch == null) return;

    ActionNode current = batch.head;

    if (batch.type == ActionType.INSERT) {
        while (current != null) {
            Action a = current.action;
            a.node.prev.next = a.node;
            a.node.next.prev = a.node;
            cursor = a.node;
            current = current.next;
        }
    } else {
        while (current != null) {
            Action a = current.action;
            a.node.prev.next = a.node.next;
            a.node.next.prev = a.node.prev;
            cursor = a.node.prev;
            current = current.next;
        }
    }
    undoStack.push(batch);
}

    public void display() {
        System.out.print("Editor: ");
        CharNode temp = head.next;
        while (temp != tail) {
            if (temp == cursor.next) System.out.print("|");
            System.out.print(temp.ch);
            temp = temp.next;
        }
        if (cursor.next == tail) System.out.print("|");
        System.out.println();
    }

    private void recordAction(Action action) {
        long now = System.currentTimeMillis();
        ActionBatch last = undoStack.peek();
        if (last != null && last.type == action.type && now - last.lastTime <= BATCH_TIME) {
            last.addAction(action);
            return;
        }
        ActionBatch batch = new ActionBatch(action.type);
        batch.addAction(action);
        undoStack.push(batch);
    }

    private void breakBatch() {
        ActionBatch last = undoStack.peek();
        if (last != null) last.lastTime = 0;
    }

    public static void main(String[] args) {
        AdvancedTextEditor editor = new AdvancedTextEditor();
        Scanner sc = new Scanner(System.in);
        System.out.println("Commands: TYPE text, back (delete), left, right, undo, redo, exit");

        while (true) {
            editor.display();
            System.out.print("> ");
            String input = sc.nextLine();

            if (input.equals("exit")) break;
            else if (input.equals("undo")) editor.undo();
            else if (input.equals("redo")) editor.redo();
            else if (input.equals("left")) editor.moveLeft();
            else if (input.equals("right")) editor.moveRight();
            else if (input.equals("back")) editor.delete();
            else {
                for (char c : input.toCharArray()) {
                    editor.write(c);
                }
            }
        }
        sc.close();
    }
    
}
