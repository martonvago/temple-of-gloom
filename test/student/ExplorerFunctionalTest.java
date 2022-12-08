//package student;
//
//import game.GameState;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.nio.file.Path;
//import java.util.Random;
//
//public class ExplorerFunctionalTest {
//    /**
//     Single test that runs the game using a random seed
//     */
//    @Test
//    void runOnce() throws IOException {
//        var rng = new Random();
//        rng.setSeed(336249467);
//        GameState.runNewGame(rng.nextInt(), true);
//    }
//
//    /**
//    Runs the game From a serialised level files.
//    verifies that a level can be loaded and the explorer algorithm can find the exit for it
//    */
//    @Test
//    void RunGameFromSerialisedLevelFile() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        GameState game = new GameState(
//                Path.of("./test/resources/map_1/explore_s.txt"),
//                Path.of("./test/resources/map_1/escape_s.txt"), false);
//
//        Method RunGame = game.getClass().getDeclaredMethod("run");
//        RunGame.setAccessible(true);
//        RunGame.invoke(game);
//    }
//
//    @Test
//    void RunGameExplore() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        GameState game = new GameState(
//                Path.of("./test/resources/nightmare/explore_s.txt"),
//                Path.of("./test/resources/map_1/escape_s.txt"), false);
//
//        Method RunGame = game.getClass().getDeclaredMethod("explore");
//        RunGame.setAccessible(true);
//        RunGame.invoke(game);
//    }
//
//    /**
//     Runs the game twice From a serialised level files.
//     The first sets of levels have randomised edge weighting
//     Second Set of levels have a uniform edge weighting of 1
//     The Third set of levels have a uniform edge weighting of 10
//
//     As the physical design and gold distribution is identical, assert that map 2 scores the highest, map 3 the lowest and map 1 in the middle
//    */
//    @Test
//    void UniformWeightTest() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        GameState map1Game = new GameState(
//                java.nio.file.Path.of("./test/resources/map_1/explore_s.txt"),
//                Path.of("./test/resources/map_1/escape_s.txt"),false);
//
//        Method runGameMap1 = map1Game.getClass().getDeclaredMethod("run");
//        runGameMap1.setAccessible(true);
//        runGameMap1.invoke(map1Game);
//
//        GameState map2Game = new GameState(
//                java.nio.file.Path.of("./test/resources/map_2/explore_s.txt"),
//                Path.of("./test/resources/map_2/escape_s.txt"),false);
//
//        Method runGameMap2 = map2Game.getClass().getDeclaredMethod("run");
//        runGameMap2.setAccessible(true);
//        runGameMap2.invoke(map2Game);
//
//        GameState map3Game = new GameState(
//                java.nio.file.Path.of("./test/resources/map_3/explore_s.txt"),
//                Path.of("./test/resources/map_3/escape_s.txt"),false);
//
//        Method runGameMap3 = map3Game.getClass().getDeclaredMethod("run");
//        runGameMap3.setAccessible(true);
//        runGameMap3.invoke(map3Game);
//    }
//
//}
//