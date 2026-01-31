import java.util.*;

class Tab {
    String id;
    String url;
    String group;
    long lastActive;

    Tab(String id, String url, String group) {
        this.id = id;
        this.url = url;
        this.group = group;
        this.lastActive = System.currentTimeMillis();
    }
}

class TabNode {
    Tab tab;
    TabNode next;
    TabNode prev;

    TabNode(Tab tab) {
        this.tab = tab;
        this.next = this;
        this.prev = this;
    }
}

class SessionSnapshot {
    List<Tab> tabs;
    String activeTabId;

    SessionSnapshot(List<Tab> tabs, String activeTabId) {
        this.tabs = tabs;
        this.activeTabId = activeTabId;
    }
}

public class BrowserManager {
    private TabNode head;
    private TabNode current;
    private int size = 0;
    private final int MAX_TABS;

    private Map<String, TabNode> tabById = new HashMap<>();
    private Map<String, List<TabNode>> groupMap = new HashMap<>();
    private Stack<SessionSnapshot> sessionStack = new Stack<>();

    private int tabCounter = 1; // For auto ID generation

    public BrowserManager(int maxTabs) {
        this.MAX_TABS = maxTabs;
    }

    private String generateTabId() {
        return "T" + (tabCounter++);
    }

    public void open(String url, String group) {
        String id = generateTabId();
        Tab newTab = new Tab(id, url, group);
        TabNode newNode = new TabNode(newTab);

        if (head == null) {
            head = newNode;
            current = newNode;
        } else {
            TabNode nextNode = current.next;

            current.next = newNode;
            newNode.prev = current;
            newNode.next = nextNode;
            nextNode.prev = newNode;

            current = newNode;
        }

        tabById.put(id, newNode);

        if (group != null) {
            groupMap.computeIfAbsent(group, g -> new ArrayList<>()).add(newNode);
        }

        size++;

        enforceLimit(); // Enforce memory limit after open

        System.out.println("Tab " + id + " opened: " + url + (group != null ? " (Group: " + group + ")" : ""));
    }

    public void close() {
        if (current == null) {
            System.out.println("No tab to close.");
            return;
        }

        System.out.println(current.tab.id + " closed.");

        removeNode(current);
    }

    public void next() {
        if (current == null) return;

        current = current.next;
        current.tab.lastActive = System.currentTimeMillis();

        System.out.println("Switched to next tab: " + current.tab.id);
    }

    public void prev() {
        if (current == null) return;

        current = current.prev;
        current.tab.lastActive = System.currentTimeMillis();

        System.out.println("Switched to previous tab: " + current.tab.id);
    }

    public void switchTo(String id) {
        TabNode node = tabById.get(id);
        if (node == null) {
            System.out.println("Tab " + id + " not found.");
            return;
        }
        current = node;
        current.tab.lastActive = System.currentTimeMillis();

        System.out.println("Switched to tab " + id);
    }

    public void switchToGroup(String group) {
        List<TabNode> tabs = groupMap.get(group);
        if (tabs == null || tabs.isEmpty()) {
            System.out.println("Group '" + group + "' not found.");
            return;
        }
        current = tabs.get(0);
        current.tab.lastActive = System.currentTimeMillis();

        System.out.println("Switched to group " + group);
    }

    public void snapshot() {
        if (head == null) {
            System.out.println("No tabs to snapshot.");
            return;
        }

        List<Tab> copy = new ArrayList<>();
        TabNode temp = head;
        do {
            Tab t = temp.tab;
            Tab newTab = new Tab(t.id, t.url, t.group);
            newTab.lastActive = t.lastActive;
            copy.add(newTab);
            temp = temp.next;
        } while (temp != head);

        sessionStack.push(new SessionSnapshot(copy, current.tab.id));

        System.out.println("Session saved (" + size + " tabs).");
    }

    public void restore() {
        if (sessionStack.isEmpty()) {
            System.out.println("No session to restore.");
            return;
        }
        SessionSnapshot snap = sessionStack.pop();
        clearAll();

        for (Tab t : snap.tabs) {
            open(t.url, t.group); // open generates new ID, but we want to preserve IDs for restore
        }

        // We have to find tab node by old ID and set current
        for (TabNode node : tabById.values()) {
            if (node.tab.id.equals(snap.activeTabId)) {
                current = node;
                break;
            }
        }

        System.out.println("Session restored (" + size + " tabs).");
    }

    public void prune() {
        if (head == null) return;

        Map<String, TabNode> best = new HashMap<>();
        List<TabNode> removeList = new ArrayList<>();

        TabNode temp = head;
        do {
            String url = temp.tab.url;
            if (!best.containsKey(url)) {
                best.put(url, temp);
            } else {
                TabNode old = best.get(url);
                if (temp.tab.lastActive > old.tab.lastActive) {
                    removeList.add(old);
                    best.put(url, temp);
                } else {
                    removeList.add(temp);
                }
            }
            temp = temp.next;
        } while (temp != head);

        for (TabNode node : removeList) {
            removeNode(node);
        }
        System.out.println("Pruned duplicates.");
    }

    private void enforceLimit() {
        if (size <= MAX_TABS) return;

        TabNode lru = findLRU();
        if (lru != null) {
            System.out.println("LRU limit exceeded. Closing tab " + lru.tab.id);
            removeNode(lru);
        }
    }

    private TabNode findLRU() {
        if (head == null) return null;

        TabNode lru = head;
        TabNode temp = head.next;
        do {
            if (temp.tab.lastActive < lru.tab.lastActive) {
                lru = temp;
            }
            temp = temp.next;
        } while (temp != head);

        return lru;
    }

    public void status() {
        if (head == null) {
            System.out.println("No tabs open.");
            return;
        }

        TabNode temp = head;
        System.out.println("Tabs Status:");
        do {
            String activeMark = (temp == current) ? " <- ACTIVE" : "";
            String groupInfo = (temp.tab.group != null) ? " | " + temp.tab.group : "";
            System.out.println(temp.tab.id + ": " + temp.tab.url + groupInfo + activeMark);
            temp = temp.next;
        } while (temp != head);
    }

    private void removeNode(TabNode node) {
        tabById.remove(node.tab.id);

        if (node.tab.group != null) {
            List<TabNode> g = groupMap.get(node.tab.group);
            g.remove(node);
            if (g.isEmpty()) {
                groupMap.remove(node.tab.group);
            }
        }

        if (size == 1) {
            head = null;
            current = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            if (node == head) head = node.next;
            if (node == current) current = node.next;
        }
        size--;
    }

    private void clearAll() {
        head = null;
        current = null;
        size = 0;
        tabById.clear();
        groupMap.clear();
        tabCounter = 1;
    }

    // For Testing Convenience: CLI parsing from console
    public void commandLoop() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Session-Aware Browser Tab Manager!");
        System.out.println("Commands: OPEN <url> [group], CLOSE, NEXT, PREV, SWITCH <tab_id>, SWITCHGROUP <group>, SNAPSHOT, RESTORE, PRUNE, STATUS, EXIT");

        while (true) {
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String cmd = parts[0].toUpperCase();

            try {
                switch (cmd) {
                    case "OPEN":
                        if (parts.length == 2) {
                            open(parts[1], null);
                        } else if (parts.length >= 3) {
                            open(parts[1], parts[2]);
                        } else {
                            System.out.println("Usage: OPEN <url> [group]");
                        }
                        break;
                    case "CLOSE":
                        close();
                        break;
                    case "NEXT":
                        next();
                        break;
                    case "PREV":
                        prev();
                        break;
                    case "SWITCH":
                        if (parts.length >= 2) {
                            switchTo(parts[1]);
                        } else {
                            System.out.println("Usage: SWITCH <tab_id>");
                        }
                        break;
                    case "SWITCHGROUP":
                        if (parts.length >= 2) {
                            switchToGroup(parts[1]);
                        } else {
                            System.out.println("Usage: SWITCHGROUP <group>");
                        }
                        break;
                    case "SNAPSHOT":
                        snapshot();
                        break;
                    case "RESTORE":
                        restore();
                        break;
                    case "PRUNE":
                        prune();
                        break;
                    case "STATUS":
                        status();
                        break;
                    case "EXIT":
                        System.out.println("Exiting...");
                        sc.close();
                        return;
                    default:
                        System.out.println("Unknown command.");
                }
            } catch (Exception e) {
                System.out.println("Error processing command: " + e.getMessage());
            }
        }
    }

    // For quick testing
    public static void main(String[] args) {
        BrowserManager manager = new BrowserManager(5); // max 5 tabs allowed
        manager.commandLoop();
    }
}
