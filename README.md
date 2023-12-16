# Yogi-Bear
# Game Project README

## Overview
This project is a graphical game application developed in Java, featuring a main window with game statistics and a game board. It involves player navigation, ranger movements, and collision detection, creating an interactive and dynamic gaming experience.

### Main Components
- **MainWindow**: A `JFrame`-based class that initiates the window dimensions and includes exit functionality. It consists of two main parts: the Game Statistics Label and the Game Board, both implemented using `JPanel`.
- **Board**: Extends `JPanel` to create the game board. It transforms the model data into a user interface.
- **Game**: Handles game data management, including reading from files and iterating through levels.
- **GameLevel**: The core class for game functionality, managing rangers, players, and game logic.

### Features
- Player and ranger movements with collision detection.
- Dynamic game statistics updates.
- Level progression and data handling.

## Getting Started

### Prerequisites
- Java Runtime Environment (JRE) and Java Development Kit (JDK)
- Basic understanding of Java and Swing framework

### Installation
1. Clone the repository to your local machine.
2. Compile the Java files in your preferred IDE or using the Java compiler.
3. Run the `MainWindow` class to start the game.

## Usage
- Use `W`, `A`, `S`, `D` or arrow keys to move the player on the board.
- Observe the game statistics for player progress and game state.
- Interact with the game menu for additional options.

## License
This project is licensed under the [MIT License](LICENSE.md).

