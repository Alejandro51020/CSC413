# CSC 413 Chess Engine Design

## Class Diagram

The following diagram shows the classes in the `engine` and `model` packages after M5.

```mermaid
classDiagram
    direction TB

    class Game {
        -Board board
        -List~Move~ history
        -Color sideToMove
        +Game()
        +Game(Board board, Color sideToMove)
        +Board board()
        +Color sideToMove()
        +List~Move~ history()
        +List~Move~ legalMoves()
        +Optional~Move~ findLegalMove(String notation)
        +void play(Move move)
        +Optional~Move~ undoLastMove()
    }

    class MoveGenerator {
        <<utility>>
        +List~Move~ legalMoves(Board board, Color color)$
        ~List~Move~ pseudoLegalMoves(Board board, Color color)$
        +boolean isAttacked(Board board, Position target, Color attacker)$
        +boolean isInCheck(Board board, Color color)$
    }

    class Board {
        -Piece[][] squares
        +Board()
        +Piece pieceAt(Position position)
        +boolean isEmpty(Position position)
        +void place(Position position, Piece piece)
        +void apply(Move move)
        +void undo(Move move)
        +List~Position~ positionsOf(Color color)
        +Position kingPosition(Color color)
        +String toString()
    }

    class Piece {
        <<abstract>>
        -Color color
        -PieceType type
        +Color color()
        +PieceType type()
        +char symbol()
        +List~Move~ pseudoLegalMoves(Board board, Position from)*
        +boolean attacks(Board board, Position from, Position target)
        +String toString()
    }

    class Pawn
    class Knight
    class Bishop
    class Rook
    class Queen
    class King

    class Move {
        <<record>>
        +Position from
        +Position to
        +Piece moved
        +Piece captured
        +PieceType promotesTo
        +Move quiet(Position from, Position to, Piece moved)$
        +Move capture(Position from, Position to, Piece moved, Piece captured)$
        +Move promotion(Position from, Position to, Piece moved, Piece captured, PieceType promotesTo)$
        +boolean isCapture()
        +boolean isPromotion()
        +String toString()
    }

    class Position {
        <<record>>
        +int file
        +int rank
        +int BOARD_SIZE$
        +boolean isOnBoard(int file, int rank)$
        +Position parse(String algebraic)$
        +Position offsetOrNull(int fileDelta, int rankDelta)
        +String toString()
    }

    class Color {
        <<enumeration>>
        WHITE
        BLACK
        +Color opposite()
        +int pawnDirection()
        +int pawnStartRank()
        +int promotionRank()
    }

    class PieceType {
        <<enumeration>>
        PAWN
        KNIGHT
        BISHOP
        ROOK
        QUEEN
        KING
        +char symbol()
        +PieceType fromSymbol(char letter)$
    }

    Piece <|-- Pawn
    Piece <|-- Knight
    Piece <|-- Bishop
    Piece <|-- Rook
    Piece <|-- Queen
    Piece <|-- King

    Board *-- Piece : squares

    Game *-- Board : board
    Game --> Color : sideToMove
    Game o-- Move : history
    Game ..> MoveGenerator : uses

    MoveGenerator ..> Board : uses
    MoveGenerator ..> Color : uses
    MoveGenerator ..> Move : creates lists of
    MoveGenerator ..> Piece : asks for moves and attacks
    MoveGenerator ..> Position : uses

    Move --> Position : from and to
    Move --> Piece : moved and captured
    Move --> PieceType : promotesTo

    Piece --> Color : color
    Piece --> PieceType : type
    Piece ..> Board : examines
    Piece ..> Position : moves from
    Piece ..> Move : creates

    Board ..> Position : indexes squares
    Board ..> Color : searches by color
    Board ..> Move : applies and undoes
    Board ..> PieceType : promotion type
```

## Sequence Diagram

The following diagram shows how `game.legalMoves()` works after M5. `MoveGenerator` first asks every piece for its pseudo-legal moves. It then temporarily applies each candidate move, checks whether the moving side's king is attacked, restores the board, and either keeps or drops the candidate.

```mermaid
sequenceDiagram
    actor Caller
    participant Game
    participant MoveGenerator
    participant Board
    participant Piece

    Caller->>Game: legalMoves()
    Game->>MoveGenerator: legalMoves(board, sideToMove)

    MoveGenerator->>MoveGenerator: pseudoLegalMoves(board, color)
    MoveGenerator->>Board: positionsOf(color)
    Board-->>MoveGenerator: occupied positions

    loop each position belonging to color
        MoveGenerator->>Board: pieceAt(position)
        Board-->>MoveGenerator: piece
        MoveGenerator->>Piece: pseudoLegalMoves(board, position)
        Piece-->>MoveGenerator: piece moves
    end

    MoveGenerator-->>MoveGenerator: candidate moves

    loop each candidate move
        MoveGenerator->