package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

import static my.application.stephen.runattackdungeon.GameView.Noises;
import static my.application.stephen.runattackdungeon.GameView.camHeight;
import static my.application.stephen.runattackdungeon.GameView.camOffsetX;
import static my.application.stephen.runattackdungeon.GameView.camOffsetY;
import static my.application.stephen.runattackdungeon.GameView.camWidth;
import static my.application.stephen.runattackdungeon.GameView.changeLighting;
import static my.application.stephen.runattackdungeon.GameView.changeMap;
import static my.application.stephen.runattackdungeon.GameView.friendlyFire;
import static my.application.stephen.runattackdungeon.GameView.idMiningFail;
import static my.application.stephen.runattackdungeon.GameView.idMiningSucceed;
import static my.application.stephen.runattackdungeon.GameView.idMinotaurRoar;
import static my.application.stephen.runattackdungeon.GameView.idWalk;
import static my.application.stephen.runattackdungeon.GameView.imageClutter;
import static my.application.stephen.runattackdungeon.GameView.imageWeapon;
import static my.application.stephen.runattackdungeon.GameView.mBitMapWidth;
import static my.application.stephen.runattackdungeon.GameView.screenHeight;
import static my.application.stephen.runattackdungeon.GameView.screenWidth;
import static my.application.stephen.runattackdungeon.GameView.spaces;
import static my.application.stephen.runattackdungeon.Level.roomHeightMin;
import static my.application.stephen.runattackdungeon.Level.roomWidthMin;
import static my.application.stephen.runattackdungeon.Map.rand;

/**
 * Created by zfile on 2018-03-02.
 * Dungeon class used to contain levels and specify the behavior and generation therein.
 */

public class Dungeon {

    public enum DirectionToGo {NORTH, SOUTH, EAST , WEST, DOWN, UP}
    //the Levels
    static boolean minotaurSlain = false;
    private int numLevels = 100;
    private ArrayList<Level> dungeonLevels = new ArrayList<>(numLevels);
    private Level currentLevel;
    private int currentLevelIndex = 0;
    private Creature player = null;

    Dungeon(){
        minotaurSlain = false;
        AddNewLevel();
        currentLevel = dungeonLevels.get(0);
    }

    //Accessors
    public ArrayList<Level> getDungeonLevels() {return dungeonLevels;}
    public Level getCurrentLevel() {return currentLevel;}
    Creature getPlayer() {return player;}
    //Mutators
    public void setPlayer(Creature newPlayer){player = newPlayer;}
    //Helper Functions
    public void UpdateCreatures() {
        for (int dungeonIndex = 0; dungeonIndex < dungeonLevels.size(); dungeonIndex++) {
            ArrayList<Creature> dungeonLevelCreatures = dungeonLevels.get(dungeonIndex).getLevelCreatures();
            int listSize = dungeonLevelCreatures.size() - 1;
//            if (autoPlay){
//                listSize++;
//            }
            for (int creatureListIndex = 0; creatureListIndex < listSize; creatureListIndex++) {
                Creature critter = dungeonLevelCreatures.get(creatureListIndex);
                if (critter.getDirectionType() != Creature.DirectionType.Still) {
                    moveCreatureHandicap(critter);
                }
            }
        }
    }

    private void moveCreatureHandicap(Creature critter) {
        switch (critter.getHandicap()) {
            case OneWaitTwo:
                switch (critter.getHandicapWait()) {
                    default:
                    case 0:
                        critter.setHandicapWait(1);
                        moveCreatureMovementLimit(critter);
                        break;
                    case 1:
                        critter.setHandicapWait(2);
                        break;
                    case 2:
                        critter.setHandicapWait(0);
                        break;
                }
                break;
            case OneWaitOne:
                switch (critter.getHandicapWait()) {
                    default:
                    case 0:
                        critter.setHandicapWait(1);
                        moveCreatureMovementLimit(critter);
                        break;
                    case 1:
                        critter.setHandicapWait(0);
                        break;
                }
                break;
            case TwoWaitTwo:
                switch (critter.getHandicapWait()) {
                    default:
                    case 0:
                        critter.setHandicapWait(1);
                        moveCreatureMovementLimit(critter);
                    case 1:
                        critter.setHandicapWait(2);
                        moveCreatureMovementLimit(critter);
                        break;
                    case 2:
                        critter.setHandicapWait(3);
                        break;
                    case 3:
                        critter.setHandicapWait(0);
                        break;
                }
                break;
            case TwoWaitOne:
                switch (critter.getHandicapWait()) {
                    default:
                    case 0:
                        critter.setHandicapWait(1);
                        moveCreatureMovementLimit(critter);
                    case 1:
                        critter.setHandicapWait(2);
                        moveCreatureMovementLimit(critter);
                        break;
                    case 2:
                        critter.setHandicapWait(0);
                        break;
                }
                break;
            default:
            case Full:
                moveCreatureMovementLimit(critter);
                break;
        }
    }

    private void moveCreatureMovementLimit(Creature critter) {
        switch (critter.getMovementLimit()) {
            default:
            case inCamera:
                if (critter.getX() > camOffsetX &&
                        critter.getY() > camOffsetY &&
                        critter.getX() < camOffsetX + camWidth &&
                        critter.getY() < camOffsetY + camHeight) {
                    updateCreatureState(critter);
                }
                break;
            case inLevel:
                if (critter.getTarget().z == critter.getZ()) {
                    updateCreatureState(critter);
                }
                break;
            case inDungeon:
                if (critter.getTarget().z < critter.getZ()) {
                    critter.setMiningTool(new MiningTool(15, 15, critter.getPoint(), imageWeapon[0], 15));
                    critter.setTarget(dungeonLevels.get(critter.getZ()).getStairsUp().getPoint());
                } else if (critter.getTarget().z > critter.getZ()) {
                    critter.setMiningTool(new MiningTool(15, 15, critter.getPoint(), imageWeapon[0], 15));
                    critter.setTarget(dungeonLevels.get(critter.getZ()).getStairsDown().getPoint());
                } else if (critter.getMiningTool().getBitmap() == imageWeapon[0]) {
                    critter.setMiningTool(null);
                }
                updateCreatureState(critter);
                break;
            case inWorld:
                //TODO: implement world.
                break;
        }
    }

    private void updateCreatureState(Creature critter) {
        switch (critter.getCreatureState()) {
            case Rest:
                critter.setPatrolState(critter.getPatrolState() + 1);
                int restTime = 30;
                switch (critter.getCellType()){
                    default:
                    case Slime:
                        break;
                    case Goblin:
                        restTime = 5;
                        break;
                    case Minotaur:
                        restTime = 2;
                        break;
                    case Humanoid:
                        restTime = 1;
                        break;
                }
                if (critter.getPatrolState() > restTime) {
                    critter.setCreatureState(Creature.state.Wander);
                }
                setChasePlayer(critter);
                break;
            case Patrol:
                switch (critter.getPatrolType()) {
                    case Points:
                        //Let us hope that the critter has been designated as MovementType.Towards...etc.
                        if (critter.getPoint().x != critter.getTarget().x &&
                                critter.getPoint().y != critter.getTarget().y) {
                            critter.setTarget(
                                    critter.getPatrolPoints().get(
                                            critter.getPatrolState()
                                    )
                            );
                            moveCreatureDirectionType(critter.getTarget(), critter);
                        } else {
                            if (critter.getPatrolState() < critter.getPatrolPoints().size() - 1) {
                                critter.setPatrolState(critter.getPatrolState() + 1);
                            } else {
                                critter.setPatrolState(0);
                            }
                        }
                        break;
                    case Line:
                        switch (critter.getDirectionType()) {
                            case Still:
                                break;
                            case Vertical:
                                switch (critter.getPatrolState()) {
                                    default:
                                    case 0:
                                        if (!MoveCreatureVertical(critter, critter.getY() + 1)) {
                                            critter.setPatrolState(1);
                                        }
                                        break;
                                    case 1:
                                        if (!MoveCreatureVertical(critter, critter.getY() - 1)) {
                                            critter.setPatrolState(0);
                                        }
                                        break;
                                }
                                break;
                            case Horizontal:
                                switch (critter.getPatrolState()) {
                                    default:
                                    case 0:
                                        if (!MoveCreatureHorizontal(critter, critter.getX() + 1)) {
                                            critter.setPatrolState(1);
                                        }
                                        break;
                                    case 1:
                                        if (!MoveCreatureHorizontal(critter, critter.getX() - 1)) {
                                            critter.setPatrolState(0);
                                        }
                                        break;
                                }
                                break;
                            case HorizontalAndVertical:
                                switch (critter.getPatrolState()) {
                                    default:
                                    case 0:
                                        if (!MoveCreatureVertical(critter, critter.getY() + 1)) {
                                            critter.setPatrolState(1);
                                        }
                                        break;
                                    case 1:
                                        if (!MoveCreatureHorizontal(critter, critter.getX() + 1)) {
                                            critter.setPatrolState(2);
                                        }
                                        break;
                                    case 2:
                                        if (!MoveCreatureVertical(critter, critter.getY() - 1)) {
                                            critter.setPatrolState(3);
                                        }
                                        break;
                                    case 3:
                                        if (!MoveCreatureHorizontal(critter, critter.getX() - 1)) {
                                            critter.setPatrolState(0);
                                        }
                                        break;
                                }
                                break;
                        }
                        break;
                }
                setChasePlayer(critter);
                break;
            case Chase:
                moveCreatureDirectionType(critter.getTarget(), critter);
                if (dungeonLevels.get(critter.getZ()).distance(
                        new Point(critter.getTarget().x, critter.getTarget().y), critter.get2dPoint()
                ) > 20 * mBitMapWidth) {
                    critter.setCreatureState(Creature.state.Patrol);
                }
                break;
            case Gather:
                Point3d newTarget = null;
                double distanceToNewTarget = Double.MAX_VALUE;
                double possibleDistance;
                Level critterLevel = dungeonLevels.get(critter.getZ());
                ArrayList<Weapon> weapons = critterLevel.getWeapons();
                for (int i = 0; i < weapons.size(); i++) {
                    Point possibleTarget = weapons.get(i).get2dPoint();
                    possibleDistance = critterLevel.distance(critter.get2dPoint(), possibleTarget);
                    if (possibleDistance < distanceToNewTarget) {
                        distanceToNewTarget = possibleDistance;
                        newTarget = weapons.get(i).getPoint();
                    }
                }
                if (newTarget == null) {
                    ArrayList<Creature> levelCreatures = critterLevel.getLevelCreatures();
                    for (int i = 0; i < levelCreatures.size(); i++) {
                        Point possibleTarget = levelCreatures.get(i).get2dPoint();
                        possibleDistance = critterLevel.distance(critter.get2dPoint(), possibleTarget);
                        if (possibleDistance < distanceToNewTarget) {
                            distanceToNewTarget = possibleDistance;
                            newTarget = levelCreatures.get(i).getPoint();
                        }
                    }
                }
                if (newTarget != null) {
                    critter.setTarget(newTarget);
                    moveCreatureDirectionType(critter.getTarget(), critter);
                } else {
                    critter.setCreatureState(Creature.state.Chase);
                    updateCreatureState(critter);
                    break;
                }
                if (critter.getWeapon() != null) {
                    critter.setCreatureState(Creature.state.Chase);
                }
                break;
            case Wander:
                moveCreatureDirectionType(critter.getTarget(), critter);
                critter.setPatrolState(critter.getPatrolState() + 1);
                int wanderTime = 5;
                switch (critter.getCellType()){
                    default:
                    case Slime:
                        break;
                    case Goblin:
                        wanderTime = 10;
                        break;
                    case Minotaur:
                        wanderTime = 30;
                        break;
                    case Humanoid:
                        wanderTime = 10;
                        break;
                }
                if (critter.getPatrolState() > wanderTime) {
                    critter.setCreatureState(Creature.state.Rest);
                }
                setChasePlayer(critter);
                break;
        }
    }

    public boolean findInPath(ArrayList<Point3d> path, Point3d target) {
//        boolean isFound = false;
//        for (int i = 0; i < path.size(); i++) {
//            if (target == path.get(i)) {
//                isFound = true;
//                break;
//            }
//        }
//        return isFound;
        for (Point3d aPoint : path) {
            if (target == aPoint) {
                return true;
            }
        }
        return false;
    }

    private void setChasePlayer(Creature critter) {
        int detectionRange = 1;
        switch (critter.getCellType()) {
            case Slime:
                detectionRange = 1;
                break;
            case Goblin:
                detectionRange = 3;
                if (critter.getLightSource() != null){
                    if (detectionRange < critter.getLightSource().getLightRadius()) {
                        detectionRange = critter.getLightSource().getLightRadius();
                    }
                }
                break;
            case Minotaur:
                detectionRange = 6;
                break;
            case Humanoid:
                detectionRange = 10;
                break;
        }
        if (critter.getCreatureState() == Creature.state.Patrol) {
            detectionRange *= 2;
        }
        if (dungeonLevels.get(critter.getZ()).distance(player.get2dPoint(), critter.get2dPoint()) < detectionRange) {
            switch (critter.getCellType()) {
                case Slime:
                    critter.setCreatureState(Creature.state.Chase);
                    break;
                case Goblin:
                    if (critter.getWeapon() == null) {
                        critter.setMovementType(Creature.MovementType.TowardsTargetDirectional);
                        critter.setCreatureState(Creature.state.Gather);
                    } else {
                        critter.setCreatureState(Creature.state.Chase);
                    }
                    break;
                case Minotaur:
                    if (critter.getWeapon() == null) {
                        critter.setMovementType(Creature.MovementType.TowardsTargetDirectional);
                        critter.setCreatureState(Creature.state.Gather);
                    } else {
                        critter.setCreatureState(Creature.state.Chase);
                    }
                    break;
            }
        }
    }

    private void moveCreatureDirectionType(Point3d target, Creature temp) {
        switch (temp.getDirectionType()) {
            default:
            case Still:
                break;
            case Vertical:
                //get points above OR below current point.
                //if (point.y > otherpoint.y) {point.y = otherpoint.y} else if
                // (point.y = otherpoint.y) {point.y = point.y}
                moveCreatureMovementType(target, temp, rand.nextInt(2));
                break;
            case Horizontal:
                //get points left OR right of current point.
                //if (point.y > otherpoint.y) {point.y = otherpoint.y} else if
                // (point.y = otherpoint.y) {point.y = point.y}
                moveCreatureMovementType(target, temp, rand.nextInt(2) + 2);
                break;
            case HorizontalAndVertical:
                moveCreatureMovementType(target, temp, rand.nextInt(4));
                break;
        }
    }

    private void moveCreatureMovementType(Point3d target, Creature temp, int directionChoice) {
        switch (temp.getMovementType()) {
            case Random:
                MoveRandomly(temp, directionChoice);
                break;
            default:
            case TowardsTargetDirectional:
                Point3d start = temp.getPoint();
                int distanceWidth = (start.x - target.x);
                int distanceHeight = (start.y - target.y);

                int direction = rand.nextInt(2);
                if (distanceWidth == 0) {
                    direction = 1;
                } else if (distanceHeight == 0) {
                    direction = 0;
                }
                switch (direction) {
                    case 0: //Horizontal
                        if (distanceWidth > 0) { //West
                            MoveCreatureHorizontal(temp, temp.getX() - 1);
                        } else if (distanceWidth < 0) { //East
                            MoveCreatureHorizontal(temp, temp.getX() + 1);
                        }
                        break;
                    case 1: //Vertical
                        if (distanceHeight > 0) { //South
                            MoveCreatureVertical(temp, temp.getY() - 1);
                        } else if (distanceHeight < 0) { //North
                            MoveCreatureVertical(temp, temp.getY() + 1);
                        }
                        break;
                }
                break;
            case TowardsTargetDodgeObstacles:
                break;
            case TowardsTargetEfficient:
                int distance = 0;
                Point3d targetDest = new Point3d(target.x, target.y, distance);
                temp.getPath().add(targetDest);
                //Find all points next to target that can be walked on.
                //  append distance to point on creation of target.
                //  append this 3d point to Path.
                while (!findInPath(temp.getPath(), temp.getPoint())) {
                    distance++;
                    Point3d pointLeft = new Point3d(target.x - 1, target.y, target.z);
                    if (AddPossiblePathPoints(pointLeft, temp, distance, targetDest)) {
                        break;
                    }
                    Point3d pointRight = new Point3d(target.x + 1, target.y, target.z);
                    if (AddPossiblePathPoints(pointRight, temp, distance, targetDest)) {
                        break;
                    }
                    Point3d pointUp = new Point3d(target.x, target.y - 1, target.z);
                    if (AddPossiblePathPoints(pointUp, temp, distance, targetDest)) {
                        break;
                    }
                    Point3d pointDown = new Point3d(target.x, target.y + 1, target.z);
                    if (AddPossiblePathPoints(pointDown, temp, distance, targetDest)) {
                        break;
                    }
                }
                //int debug = 0;
                //Iteratively find all points that can be walked on that are
                //  next to previous points,
                //  not already in Path,
                //  until getPoint() is reached.
                break;
        }
    }

    private boolean AddPossiblePathPoints(Point3d point, Creature temp, int distance, Point3d targetDest) {
        if (dungeonLevels.get(temp.getZ()).isCellOpen(point.x, point.y) && !findInPath(temp.getPath(), point)) {
            Point3d possiblePathLeft = new Point3d(point.x, point.y, distance);
            temp.getPath().add(possiblePathLeft);
            if (possiblePathLeft == targetDest) {
                return true;
            }
        }
        return false;
    }

    private void MoveRandomly(Creature temp, int direction) {
        switch (direction) {
            //South
            case 0:
                MoveCreatureVertical(temp, temp.getY() + 1);
                break;
            //North
            case 1:
                MoveCreatureVertical(temp, temp.getY() - 1);
                break;
            //West
            case 2:
                MoveCreatureHorizontal(temp, temp.getX() + 1);
                break;
            //East
            case 3:
                MoveCreatureHorizontal(temp, temp.getX() - 1);
                break;
            //Stay
            default:
            case 4:
                break;
        }
    }

    public boolean MoveCreatureHorizontal(Creature creature, int X) {
        if (interactWithObject(
                new Point(X, creature.getPoint().y),
                creature)) {
            dungeonLevels.get(creature.getZ()).removeObjectFromMap(creature.get2dPoint(), creature);
            creature.setX(X);
            if (creature.getZ() == player.getZ()) {
                Noises.play(idWalk, 1, 1, 0, 0, 1);
//              walkingNoises[0].start();
            }
            dungeonLevels.get(creature.getZ()).addObjectToMap(creature.get2dPoint(), creature, false);
            if (creature.getLightSource() != null &&
                    currentLevel == dungeonLevels.get(creature.getZ() )
                    ){
                changeLighting = true;
            }
            return true;
        }
        return false;
    }

    public boolean MoveCreatureVertical(Creature creature, int Y) {
        if (interactWithObject(
                new Point(creature.getPoint().x, Y),
                creature)) {
            dungeonLevels.get(creature.getZ()).removeObjectFromMap(creature.get2dPoint(), creature);
            creature.setY(Y);
            if (creature.getZ() == player.getZ()) {
                Noises.play(idWalk, 1, 1, 0, 0, 1);
//            walkingNoises[0].start();
            }
            dungeonLevels.get(creature.getZ()).addObjectToMap(creature.get2dPoint(), creature, false);
            if (creature.getLightSource() != null &&
                    currentLevel == dungeonLevels.get(creature.getZ() )
                    ){
                changeLighting = true;
            }
            return true;
        }
        return false;
    }

    public boolean interactWithObject(Point actee, Creature actor) {
        boolean ifCreatureGetsMoved = false;
        ObjectDestructible.CellType harmeeType = dungeonLevels.get(actor.getZ()).getOtherCellType(actee.x, actee.y);
        switch (harmeeType) {
            default:
            case Border:
                Noises.play(idMiningFail, 1, 1, 0, 0, 1);
//                miningNoises[0].start();
                break;
            case Wall:
            case SturdyWall:
            case BreakingWall:
                if (actee.x >= dungeonLevels.get(actor.getZ()).getMapWidth() || actee.x < 0 || actee.y >= dungeonLevels.get(actor.getZ()).getMapHeight() || actee.y < 0) {
                    break;
                }
                dungeonLevels.get(actor.getZ()).harmWall(actee.x, actee.y,actor.getZ(),  actor.getMining(), actor.getMiningTool(), harmeeType);
                break;
            case Space:
                ifCreatureGetsMoved = true;
                break;
            case Void:
                int fallDamage = 2;
                Wearable ring = actor.getRing();
                if (ring != null) {
                    if (ring.getEnchantType() == Wearable.EnchantType.FeatherFall) {
                        fallDamage = 0;
                    }
                }
                actor.hurt(fallDamage);
                goToLevel(actor, actor.getZ() + 1, Dungeon.DirectionToGo.DOWN, true);
            case StairDown:
                goToLevel(actor, actor.getZ() + 1, Dungeon.DirectionToGo.DOWN, false);
                break;
            case StairUp:
                goToLevel(actor, actor.getZ() - 1, Dungeon.DirectionToGo.UP, false);
                break;
            case Rock:
                if (actor.getMining() > 0) {
                    Noises.play(idMiningSucceed, 1, 1, 0, 0, 1);
//                    miningNoises[1].start();
                } else {
                    Noises.play(idMiningFail, 1, 1, 0, 0, 1);
//                    miningNoises[0].start();
                }
            case Clutter:
            case Barrel:
            case Chest:
                ArrayList<Clutter> clutter = dungeonLevels.get(actor.getZ()).getClutter();
                for (int i = 0; i < clutter.size(); i++) {
                    Clutter temp = clutter.get(i);
                    Bitmap tempImage = temp.getBitmap();
                    if (temp.getX() == actee.x && temp.getY() == actee.y) {
                        if (tempImage == imageClutter[0]) {
                            temp.hurt(actor.getMining());
                        } else {
                            temp.hurt(actor.getAttack());
                        }
                        if (tempImage == imageClutter[3] || // coins
                                tempImage == imageClutter[4] || // white diamond
                                tempImage == imageClutter[5]) { // red diamond
                            ifCreatureGetsMoved = true;
                            if (tempImage != imageClutter[5]) {
                                actor.incrementScore(temp.getValue());
                            } else {
                                actor.setMaxHP(actor.getMaxHP() + 1);
                            }
                            dungeonLevels.get(actor.getZ()).removeObjectFromMap(temp.get2dPoint(), temp);
                            clutter.remove(i);
                        } else if (temp.getHP() <= 0) {
                            if (tempImage != imageClutter[0]) {
                                if (tempImage == imageClutter[1]) {
                                    dungeonLevels.get(actor.getZ()).CreateRandomDrop(i, ObjectDestructible.CellType.Barrel, actor.getZ());
                                } else if (tempImage == imageClutter[2]) {
                                    dungeonLevels.get(actor.getZ()).CreateRandomDrop(i, ObjectDestructible.CellType.Chest, actor.getZ());
                                }
                            }
                            dungeonLevels.get(actor.getZ()).removeObjectFromMap(temp.get2dPoint(), temp);
                            clutter.remove(i);
                            break;
                        }
                    }
                }
                break;
            case Slime:
                if (actor.getCellType() == ObjectDestructible.CellType.Slime && !friendlyFire) {
                    break;
                }
                dungeonLevels.get(actor.getZ()).HarmCreature(actee.x, actee.y, actor, actor.getZ(), dungeonLevels.size(), harmeeType);
                break;
            case Goblin:
                if (actor.getCellType() == ObjectDestructible.CellType.Goblin && !friendlyFire) {
                    break;
                }
                dungeonLevels.get(actor.getZ()).HarmCreature(actee.x, actee.y, actor, actor.getZ(), dungeonLevels.size(),  harmeeType);
                break;
            case Minotaur:
                if (actor.getCellType() == ObjectDestructible.CellType.Minotaur && !friendlyFire) {
                    break;
                }
                if (dungeonLevels.get(actor.getZ()).HarmCreature(actee.x, actee.y, actor, actor.getZ(), dungeonLevels.size(),  harmeeType)) {
                    if (actor == player) {
                        minotaurSlain = true;
                    }
                }
                break;
            case Humanoid:
                if (actor.getCellType() == ObjectDestructible.CellType.Humanoid && !friendlyFire) {
                    break;
                }
                dungeonLevels.get(actor.getZ()).HarmCreature(actee.x, actee.y, actor, actor.getZ(), dungeonLevels.size(),  harmeeType);
                break;
            case Weapon:
                ArrayList<Weapon> weapons = dungeonLevels.get(actor.getZ()).getWeapons();
                for (int i = 0; i < weapons.size(); i++) {
                    if (weapons.get(i).getX() == actee.x && weapons.get(i).getY() == actee.y) {
                        Weapon possibleDrop = actor.setWeapon(weapons.get(i));
                        dungeonLevels.get(actor.getZ()).removeObjectFromMap(weapons.get(i).get2dPoint(), weapons.get(i));
                        weapons.remove(i);
                        if (possibleDrop != null && possibleDrop.getBitmap() != imageWeapon[0]) {
                            //if it exists, we want to
                            //  give the weapon on the ground to the harmer (should have happened in setWeapon)
                            //  delete the weapon on the ground from weapons.
                            //  drop the swapped harmer weapon (possibleDrop)
                            possibleDrop.setPoint(actee.x, actee.y, actor.getZ());
                            weapons.add(possibleDrop);
                            dungeonLevels.get(actor.getZ()).addObjectToMap(possibleDrop.get2dPoint(), possibleDrop, true);
                        }
                        //either way, we want to move the harmer to the new point.
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
            case MiningTool:
                ArrayList<MiningTool> miningTools = dungeonLevels.get(actor.getZ()).getMiningTools();
                for (int i = 0; i < miningTools.size(); i++) {
                    if (miningTools.get(i).getX() == actee.x && miningTools.get(i).getY() == actee.y) {
                        MiningTool possibleDrop = actor.setMiningTool(miningTools.get(i));
                        dungeonLevels.get(actor.getZ()).removeObjectFromMap(miningTools.get(i).get2dPoint(), miningTools.get(i));
                        miningTools.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(actee.x, actee.y, actor.getZ());
                            miningTools.add(possibleDrop);
                            dungeonLevels.get(actor.getZ()).addObjectToMap(possibleDrop.get2dPoint(), possibleDrop, true);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
            case LightSource:
                ArrayList<LightSource> lights = dungeonLevels.get(actor.getZ()).getLights();
                for (int i = 0; i < lights.size(); i++) {
                    if (lights.get(i).getX() == actee.x && lights.get(i).getY() == actee.y) {
                        LightSource litThing = lights.get(i);
                        dungeonLevels.get(actor.getZ()).removeObjectFromMap(litThing.get2dPoint(), litThing);
                        LightSource possibleDrop = actor.setLightSource(litThing);
                        lights.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(actee.x, actee.y, actor.getZ());
                            lights.add(possibleDrop);
                            dungeonLevels.get(actor.getZ()).addObjectToMap(possibleDrop.get2dPoint(), possibleDrop, true);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
            case Wearable:
                ArrayList<Wearable> wearables = dungeonLevels.get(actor.getZ()).getWearables();
                for (int i = 0; i < wearables.size(); i++) {
                    if (wearables.get(i).getX() == actee.x && wearables.get(i).getY() == actee.y) {
                        Wearable possibleDrop = actor.setWearable(wearables.get(i));
                        dungeonLevels.get(actor.getZ()).removeObjectFromMap(wearables.get(i).get2dPoint(), wearables.get(i));
                        wearables.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(actee.x, actee.y, actor.getZ());
                            wearables.add(possibleDrop);
                            dungeonLevels.get(actor.getZ()).addObjectToMap(possibleDrop.get2dPoint(), possibleDrop, true);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;

            case Food:
                ArrayList<Food> food = dungeonLevels.get(actor.getZ()).getFood();
                for (int i = 0; i < food.size(); i++) {
                    if (food.get(i).getX() == actee.x && food.get(i).getY() == actee.y) {
                        Food possibleDrop = actor.setFood(food.get(i));
                        dungeonLevels.get(actor.getZ()).removeObjectFromMap(food.get(i).get2dPoint(), food.get(i));
                        food.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(actee.x, actee.y, actor.getZ());
                            food.add(possibleDrop);
                            dungeonLevels.get(actor.getZ()).addObjectToMap(possibleDrop.get2dPoint(), possibleDrop, true);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
            case Scroll:
                ArrayList<Clutter> scrolls = dungeonLevels.get(actor.getZ()).getScrolls();
                for (int i = 0; i < scrolls.size(); i++) {
                    if (scrolls.get(i).getX() == actee.x && scrolls.get(i).getY() == actee.y) {
                        Clutter possibleDrop = actor.setScroll(scrolls.get(i));
                        dungeonLevels.get(actor.getZ()).removeObjectFromMap(scrolls.get(i).get2dPoint(), scrolls.get(i));
                        scrolls.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(actee.x, actee.y, actor.getZ());
                            scrolls.add(possibleDrop);
                            dungeonLevels.get(actor.getZ()).addObjectToMap(possibleDrop.get2dPoint(), possibleDrop, true);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
            case Potion:
                ArrayList<Food> potions = dungeonLevels.get(actor.getZ()).getPotions();
                for (int i = 0; i < potions.size(); i++) {
                    if (potions.get(i).getX() == actee.x && potions.get(i).getY() == actee.y) {
                        Food possibleDrop = actor.setPotion(potions.get(i));
                        dungeonLevels.get(actor.getZ()).removeObjectFromMap(potions.get(i).get2dPoint(), potions.get(i));
                        potions.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(actee.x, actee.y, actor.getZ());
                            potions.add(possibleDrop);
                            dungeonLevels.get(actor.getZ()).addObjectToMap(possibleDrop.get2dPoint(), possibleDrop, true);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
        }
        return ifCreatureGetsMoved;
    }

    private void RemoveCreatureFromCurrentLevel(Creature creature) {
        for (int i = 0; i < currentLevel.getLevelCreatures().size(); i++) {
            Creature temp = currentLevel.getLevelCreatures().get(i);
            if (temp == creature) {
                currentLevel.getLevelCreatures().remove(i);
            }
        }
    }

    public void goToLevel(Creature creature, int levelToGoTo, DirectionToGo direction, boolean fallen) {
        if (levelToGoTo >= dungeonLevels.size()){
            levelToGoTo = dungeonLevels.size() - 1;
        }
        switch (direction) {
            case DOWN:
                dungeonLevels.get(creature.getZ()).removeObjectFromMap(new Point(creature.getPoint().x, creature.getPoint().y), creature);
                if (currentLevel == dungeonLevels.get(dungeonLevels.size() - 1)) {
                    AddNewLevel();
                }
                RemoveCreatureFromCurrentLevel(creature);
                if (creature == player) {
                    currentLevel = dungeonLevels.get(levelToGoTo);
                }
                creature.setZ(levelToGoTo);
                dungeonLevels.get(levelToGoTo).getLevelCreatures().add(creature);
                if (creature != null) {
                    if (creature == player && currentLevel.getStairsUp() != null && !fallen) {
                        creature.setX(currentLevel.getStairsUp().getX());
                        creature.setY(currentLevel.getStairsUp().getY());
                        dungeonLevels.get(levelToGoTo).addObjectToMap(creature.get2dPoint(), creature, true);
                    }
                    else {
                        dungeonLevels.get(levelToGoTo).giveNewPointToObject(null, creature, levelToGoTo);
                    }
                }
                if (creature == player){
                    changeMap = true;
                }
                break;
            case UP:
                if (creature.getZ() > 0) {
                    dungeonLevels.get(creature.getZ()).removeObjectFromMap(new Point(creature.getX(), creature.getY()), creature);
                    RemoveCreatureFromCurrentLevel(creature);
                    if (creature == player) {
                        currentLevel = dungeonLevels.get(levelToGoTo);
                    }
                    creature.setZ(levelToGoTo);
                    dungeonLevels.get(levelToGoTo).getLevelCreatures().add(creature);
                    if (creature != null) {
                        if (creature == player && currentLevel.getStairsDown() != null) {
                            creature.setX(currentLevel.getStairsDown().getX());
                            creature.setY(currentLevel.getStairsDown().getY());
                            dungeonLevels.get(levelToGoTo).addObjectToMap(new Point(creature.getX(), creature.getY()), creature, true);
                        } else{
                            dungeonLevels.get(levelToGoTo).giveNewPointToObject(null, creature, levelToGoTo);
                        }
                    }
                    if (creature == player){
                        changeMap = true;
                    }
                }
                break;
        }
        if (creature == player) {
            currentLevelIndex = creature.getZ();
            ArrayList<Creature> levelCreatures = dungeonLevels.get(levelToGoTo).getLevelCreatures();
            for (int i = 0; i < levelCreatures.size(); i++){
                if (levelCreatures.get(i).getCellType() == ObjectDestructible.CellType.Minotaur){
                    Noises.play(idMinotaurRoar, 1, 1, 0, 0, 1);
                    levelCreatures.get(i).setCreatureState(Creature.state.Chase);
                }
            }
        }
    }

    private void AddNewLevel() {
        Level temp;
        int Width;
        int Height;
        int borderThickness = 3;
        if (dungeonLevels.size() != 0) {
            Width = dungeonLevels.get(dungeonLevels.size() - 1).getMapWidth() + dungeonLevels.size() - 1;
            if (Width > (screenWidth / spaces[0].getHeight()) * 3) {
                Width = (screenWidth / spaces[0].getHeight()) * 3;
            }
            Height = dungeonLevels.get(dungeonLevels.size() - 1).getMapHeight() + dungeonLevels.size() - 1;
            if (Height > (screenHeight / spaces[0].getHeight()) * 3) {
                Height = (screenHeight / spaces[0].getHeight()) * 3;
            }
        } else {
            Width = screenWidth / spaces[0].getHeight();
            Height = screenHeight / spaces[0].getHeight();
        }

        if (Width < roomWidthMin){
            Width = roomWidthMin;
        }
        if (Height < roomHeightMin){
            Height = roomHeightMin;
        }
//        if (currentLevelIndex % 5 == 0 && currentLevelIndex != 0) {
//            //ShopLevel
//            temp = new Level(Width, Height, 0, false, currentLevelIndex);
//        } else if (currentLevelIndex % 3 == 0 && currentLevelIndex != 0) {
//            temp = new Level(Width, Height, 0, false, currentLevelIndex);
//        } else {
            temp = new Level(
                    Width + (borderThickness * 2),
                    Height + (borderThickness * 2),
                    dungeonLevels.size(),
                    50,
                    true,
                    true,
                    borderThickness
            );
//        }
        dungeonLevels.add(temp);
    }

//    public DirectionToGo getRandomDirection(int max, int min){
//        Random rand = new Random();
//        switch(rand.nextInt(max - min) + min){
//            default:
//            case 0:
//                return Dungeon.DirectionToGo.NORTH;
//            case 1:
//                return Dungeon.DirectionToGo.SOUTH;
//            case 2:
//                return Dungeon.DirectionToGo.EAST;
//            case 3:
//                return Dungeon.DirectionToGo.WEST;
//            case 4:
//                return Dungeon.DirectionToGo.UP;
//            case 5:
//                return Dungeon.DirectionToGo.DOWN;
//        }
//    }
}
