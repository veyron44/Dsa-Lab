# Browser Tab Manager

A session-aware browser tab management system implemented in Java. This application simulates a browser's tab functionality with features like tab navigation, grouping, snapshots for session management, and automatic LRU (Least Recently Used) eviction.

## Features

- **Tab Management**: Open, close, navigate (next/previous), and switch between tabs.
- **Tab Grouping**: Organize tabs into groups for better organization.
- **Session Snapshots**: Save and restore the entire session state, including tab order and groups.
- **LRU Eviction**: Automatically evict the least recently used tabs when the maximum tab limit is reached.
- **Pruning**: Remove duplicate tabs based on URL, keeping the most recently active one.
- **Command-Line Interface**: Interactive CLI for managing tabs with simple commands.

## Architecture

The system is built using object-oriented principles with the following key components:

### Classes

- **BrowserSessionManager**: Core class that manages the tab list, active tab, groups, and snapshots. Uses a doubly-linked circular list for efficient navigation.
- **Tab**: Represents an individual tab with properties like ID, URL, group, and last active timestamp. Implements Serializable for snapshot persistence.
- **SessionState**: A serializable data structure to capture the state of the session, including the tab list, active tab, and groups.
- **Main**: Entry point with a command-line interface for user interaction.

### Data Structures

- **Doubly-Linked Circular List**: For tab ordering and navigation.
- **HashMap for Tabs**: Quick lookup by ID.
- **HashMap for Groups**: Maps group names to sets of tab IDs.
- **Stack for Snapshots**: Stores session states for undo/restore functionality.

## Installation

1. Ensure you have Java Development Kit (JDK) installed (version 8 or higher).
2. Clone or download the project files.
3. Compile the source code:
   ```
   javac -d bin src/*.java
   ```
4. Run the application:
   ```
   java -cp bin Main
   ```

## Usage

After running the application, you'll see a command prompt (`>`). Use the following commands to manage tabs:

### Commands

- **OPEN <url> [group]**: Open a new tab with the specified URL. Optionally assign it to a group.
  - Example: `OPEN https://example.com work`
- **CLOSE**: Close the currently active tab.
- **NEXT**: Switch to the next tab in the list.
- **PREV**: Switch to the previous tab in the list.
- **SWITCH <tab_id>**: Switch to a specific tab by its ID.
  - Example: `SWITCH T1`
- **SNAPSHOT**: Save the current session state.
- **RESTORE**: Restore the last saved session state.
- **PRUNE**: Remove duplicate tabs (same URL), keeping the most recently active one.
- **STATUS**: Display the current list of tabs, with the active tab marked with `*`.
- **EXIT**: Quit the application.

### Example Session

```
Session-Aware Browser Tab Manager
Commands: OPEN, CLOSE, NEXT, PREV, SWITCH, SNAPSHOT, RESTORE, PRUNE, STATUS, EXIT
> OPEN https://google.com search
Tab T1 opened: https://google.com (Group: search)
> OPEN https://github.com dev
Tab T2 opened: https://github.com (Group: dev)
> STATUS
Tabs:
* T1: https://google.com [search]
  T2: https://github.com [dev]
> NEXT
> STATUS
Tabs:
  T1: https://google.com [search]
* T2: https://github.com [dev]
> SNAPSHOT
Session saved (2 tabs).
> CLOSE
T2 closed.
> RESTORE
Session restored (2 tabs).
> EXIT
```

## Limitations and Known Issues

- Groups with spaces in their names are not supported in the CLI (split on whitespace).
- No persistence across application restarts (snapshots are in-memory).
- Not thread-safe; designed for single-threaded use.
- Maximum tab limit is fixed at initialization (default 5 in Main.java).
- No validation for URLs or group names.

## Future Improvements

- Add URL validation and normalization.
- Support for quoted arguments in CLI to allow spaces in groups.
- Persistent storage for snapshots (e.g., to file).
- GUI interface for better usability.
- Undo/redo beyond single snapshot.
- Configuration file for settings like max tabs.
- Unit tests for all classes and edge cases.

## Contributing

Feel free to fork the repository and submit pull requests for improvements. Ensure code follows Java conventions and includes appropriate documentation.

## License

This project is open-source. Use at your own risk.