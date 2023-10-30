package core.entity.bot.factory.challenge;

import botapi.BotController;
import botapi.BotControllerFactory;
import botapi.ControllerContext;
import core.coordinates.Direction;
import core.coordinates.XY;
import core.entity.bot.OutOfFovException;

import java.util.*;

public class ArdiMaxisBot implements BotControllerFactory {

    int stepCount = 0;

    private static class Node {
        private Node previous = null;
        private final List<Node> adjacent = new ArrayList<>();
        private final XY position;

        private int f;
        private int h;
        private int g;
        private boolean unwalkable = false;
        private boolean badbeast = false;

        public Node(XY position) {
            this.position = position;
        }

        public void addAdjacent(Node[][] grid) {
            if(position.getX() > 0) {
                // left
                adjacent.add(grid[position.getX() - 1][position.getY()]);
                if(position.getY() > 0)
                    // upper left
                    adjacent.add(grid[position.getX() - 1][position.getY() - 1]);
                if(position.getY() < grid[0].length - 1)
                    // lower left
                    adjacent.add(grid[position.getX() - 1][position.getY() + 1]);
            }
            if(position.getX() < grid.length - 1) {
                // right
                adjacent.add(grid[position.getX() + 1][position.getY()]);
                if(position.getY() < grid[0].length - 1)
                    // lower right
                    adjacent.add(grid[position.getX() + 1][position.getY() + 1]);
                if(position.getY() > 0)
                    // upper right
                    adjacent.add(grid[position.getX() + 1][position.getY() - 1]);
            }
            if(position.getY() > 0) {
                // up
                adjacent.add(grid[position.getX()][position.getY() - 1]);
            }
            if(position.getY() < grid[0].length - 1) {
                // down
                adjacent.add(grid[position.getX()][position.getY() + 1]);
            }
        }

        @Override
        public String toString() {
            return String.format("{pos: %s, gVal:%d}", position, g);
        }
    }

    // A*
    private List<Node> aStar(List<Node> open, List<Node> closed, Node end) {
        while (open.size() > 0) {
            Node current = open.get(0);

            for(Node node : open) {
                if(node.f < current.f)
                    current = node;
            }

            open.remove(current);
            closed.add(current);

            Node[] adjacent = current.adjacent.toArray(new Node[0]);
            for(Node neighbor : adjacent) {
                if(!closed.contains(neighbor) && !neighbor.unwalkable) {
                    int tempG = current.g + 1;
                    if(open.contains(neighbor)) {
                        if(neighbor.g < tempG) {
                            neighbor.g = tempG;
                            neighbor.previous = current;
                        }
                    } else {
                        neighbor.g = tempG;
                        neighbor.previous = current;
                        open.add(neighbor);
                    }
                    neighbor.previous = current;
                    neighbor.h = (int)neighbor.position.distanceFrom(end.position);
                    neighbor.f = neighbor.h + neighbor.g;
                }
            }

            if(current.equals(end))
                return path(current);
        }
        return null;
    }

    private Node[][] initGrid(ControllerContext view, List<Node> energyStuff, List<Node> open) {

        XY upperRight = view.getViewUpperRight();
        XY lowerLeft = view.getViewLowerLeft();

        Node[][] grid = new Node[upperRight.getX() - lowerLeft.getX()][lowerLeft.getY() - upperRight.getY()];

        for (int i = 0; i < upperRight.getX() - lowerLeft.getX(); i++) {
            for (int j = 0; j < lowerLeft.getY() - upperRight.getY(); j++) {
                grid[i][j] = new Node(new XY(i, j));
                XY current = new XY(i + lowerLeft.getX(), j + upperRight.getY());
                if (view.getEntityAt(current) != null)
                    switch (view.getEntityAt(current)) {
                        case GOODPLANT, GOODBEAST -> energyStuff.add(grid[i][j]);
                        case BADBEAST -> {
                            grid[i][j].unwalkable = true;
                            grid[i][j].badbeast = true;
                        }
                        case MINISQUIRREL -> {
                            if (!view.isMine(current)) {
                                energyStuff.add(grid[i][j]);
                            }
                        }
                        default -> grid[i][j].unwalkable = true;
                    }
            }
        }
        return grid;
    }

    private Node initStart(XY startLoc, Node[][] grid, List<Node> open) {
        Node start = grid[startLoc.getX()][startLoc.getY()];
        for (Node[] nodes : grid) {
            for (int j = 0; j < grid[0].length; j++)
                nodes[j].addAdjacent(grid);
        }
        open.add(start);
        return start;
    }

    private Node setAim(List<Node> energyStuff, Node[][] grid,  Node start) {
        Node end = null;
        for (Node current : energyStuff) {
            if (end != null) {
                if (current.position.distanceFrom(start.position) <= end.position.distanceFrom(start.position))
                    end = grid[current.position.getX()][current.position.getY()];
            } else end = grid[current.position.getX()][current.position.getY()];
        }
        return end;
    }

    private List<Node> path(Node end) {
        List<Node> path = new ArrayList<>();
        Node temp = end;
        path.add(temp);
        while(temp.previous != null) {
            temp = temp.previous;
            path.add(temp);
        }
        return path;
    }

    private Node getNextMove(Node end) {
        Node temp = end;
        while(temp.previous != null) {
            if(temp.previous.previous == null)
                return temp;
            temp = temp.previous;
        }
        return null;
    }

    private boolean spawnMini(ControllerContext view, Node start) {
        Node[] available = defaultMoves(start);
        Random r = new Random();
        XY dir = available[r.nextInt(available.length - 1)].position;
        if(view.getEnergy() >= 100) {
            view.spawnMiniBot((Arrays.stream(Direction.values())
                    .filter(d -> d.getVector().equals(dir))
                    .findFirst()
                    .orElse(Direction.getRandom())
                    .getVector()), 100);
            return true;
        } else return false;
    }

    private Node[] defaultMoves(Node start) {
        List<Node> available = new ArrayList<>(8);
        for (Node n : start.adjacent) {
            if (n.badbeast)
                break;
            if (!n.unwalkable)
                available.add(n);
        }
        return available.toArray(new Node[0]);
    }

    @Override
    public BotController createMasterBotController() {
        return view -> {
            stepCount++;

            // just for fun
            if(stepCount % 10 == 0) {
                stepCount = 0;
                view.spawnMiniBot(Direction.getRandom().getVector(), 100);
                return;
            }

            // better readability
            XY upperRight = view.getViewUpperRight();
            XY lowerLeft = view.getViewLowerLeft();

            // init grid
            int FOVSize = (upperRight.getX() - lowerLeft.getX()) * (lowerLeft.getY() - upperRight.getY());
            List<Node> energyStuff = new ArrayList<>();
            List<Node> open = new ArrayList<>(FOVSize);
            List<Node> closed = new ArrayList<>(FOVSize);
            Node[][] grid = initGrid(view, energyStuff, open);
            // current Position of squirrel
            Node start = initStart(new XY(view.locate().getX() - lowerLeft.getX(), view.locate().getY() - upperRight.getY()), grid, open);
            // evaluate point to go
            Node end = setAim(energyStuff, grid, start);

            // if there is nothing, just spawn mini
            if (end == null) {
                spawnMini(view, start);
                return;
            }

            Direction moveDir;

            // dont even bother using the algirithm, just take 1 step
            if (start.adjacent.contains(end)) {
                XY direction = end.position.minus(start.position);
                moveDir = Arrays.stream(Direction.values())
                        .filter(dir -> dir.getVector().equals(direction))
                        .findFirst().orElse(Direction.getRandom());
            } // get into A*
            else {
                List<Node> path = aStar(open, closed, end);
                if (path != null) {
                    Node next = getNextMove(end);
                    if (next == null) {
                        moveDir = Direction.getRandom();
                    } else {
                        XY direction = next.position.minus(start.position);
                        moveDir = Arrays.stream(Direction.values())
                                .filter(dir -> dir.getVector().equals(direction))
                                .findFirst().orElse(Direction.getRandom());
                    }
                } else {
                    if(spawnMini(view, start))
                        return;
                    else
                        moveDir = Arrays.stream(Direction.values())
                                .filter(dir -> dir.getVector().equals(defaultMoves(start)[0].position))
                                .findFirst().orElse(Direction.getRandom());
                }
            }
            view.move(moveDir);
        };
    }

    @Override
    public BotController createMiniBotController() {
        return view -> {
            // implode if "necessary"
            if(20 >= view.getRemainingSteps() && view.getRemainingSteps() >= 0) {
                view.shout("... so no head?");
                view.implode(10);
                return;
            }

            // make it easier to read
            XY lowerLeft = view.getViewLowerLeft();
            XY upperRight = view.getViewUpperRight();

            // initialize
            List<Node> energyStuff = new ArrayList<>();
            List<Node> open = new ArrayList<>((upperRight.getX() - lowerLeft.getX()) * (lowerLeft.getY() - upperRight.getY()));
            List<Node> closed = new ArrayList<>();
            Node[][] grid = initGrid(view, energyStuff, open);
            Node start = initStart(new XY(view.locate().getX() - lowerLeft.getX(), view.locate().getY() - upperRight.getY()), grid, open);

            // count entities (in case an implosion is profitable)
            int entityCount = 0;
            for(int i = Math.max(start.position.getX() - 10, 0); i < Math.min(start.position.getX() + 10, grid.length); i ++) {
                for(int j = Math.max(start.position.getY() - 10,0); j < Math.min(start.position.getY() + 10, grid[0].length); j++) {
                    XY current = new XY(i + lowerLeft.getX(), j + upperRight.getY());
                        if (view.getEntityAt(current) != null)
                            switch (view.getEntityAt(current)) {
                                case GOODBEAST, BADBEAST -> entityCount += 30;
                                case GOODPLANT, BADPLANT -> entityCount += 20;
                                case MASTERSQUIRREL -> entityCount += 50;
                                case MINISQUIRREL -> entityCount += 10;
                            }
                }
            }
            // and then implode in case of profit
            if(entityCount >= 200) {
                view.implode(10);
                return;
            }

            Node end = setAim(energyStuff, grid, start);

            // if nothing nearby, just end it all
            if (end == null) {
                view.shout("UNLIMITED POWER");
                view.implode(10);
                return;
            }

            Direction moveDir;

            // dont even bother using an algorithm, just get there
            if (start.adjacent.contains(end)) {
                XY direction = end.position.minus(start.position);
                moveDir = Arrays.stream(Direction.values())
                        .filter(dir -> dir.getVector().equals(direction))
                        .findFirst().orElse(Direction.getRandom());
            } // or use the algorithm if the aim is further away
            else {
                List<Node> path = aStar(open, closed, end);
                if (path != null) {
                    Node next = getNextMove(end);
                    if (next == null) {
                        moveDir = Direction.getRandom();
                    } else {
                        XY direction = next.position.minus(start.position);
                        moveDir = Arrays.stream(Direction.values())
                                .filter(dir -> dir.getVector().equals(direction))
                                .findFirst().orElse(Direction.getRandom());
                    }
                } else {
                    view.shout("What that button do?");
                    view.implode(10);
                    return;
                }
            }
            view.move(moveDir);
        };
    }

}
