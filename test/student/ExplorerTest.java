package student;

import game.GameState;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

public class ExplorerTest {
  //  private final PrintStream standardOut = System.out;
 //   private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
//
  //  @BeforeEach
 //   void setUp() {
 //       System.setOut(new PrintStream(outputStreamCaptor));
//    }
//
   // @AfterEach
   // void tearDown() {
   //     System.setOut(standardOut);
    //}

    @Test
    void test_explore() {
        final PrintStream stdout = System.out;
        final PrintStream stderr = System.err;

        final ByteArrayOutputStream inOut = new ByteArrayOutputStream();
        final PrintStream ioStream = new PrintStream(inOut);
        System.setOut(ioStream);
        System.setErr(ioStream);

        var numRuns = 100;
        AtomicInteger failEscape = new AtomicInteger();

        var multiplers = new ArrayList<Integer>(numRuns);
        var golds = new ArrayList<Integer>(numRuns);
        var scores = new ArrayList<Integer>(numRuns);
        try {
            var rng = new Random();
            rng.setSeed(25761623242424242L);

            LongStream.generate(rng::nextLong)
                    .limit(numRuns)
                    .forEach(l ->
                            GameState.runNewGame(l, false)
                    );

            var re = Pattern.compile(
                    "^(Sc|Go|Bo)(?:nus[^:]+: 1\\.?|(?:ld|ore)[^:]+: )([0-9]+)?$"
            );
            var logs = inOut.toString();
            stdout.println(logs);

            logs.lines().forEach(s -> {
                if (s.startsWith("Your solution to explore")) {
                    scores.add(0);
                    return;
                } else if (s.startsWith("Your solution to escape")) {
                    failEscape.set(failEscape.get() + 1);
                }

                var m = re.matcher(s);
                if (m.matches()) {
                    switch (m.group(1)) {
                        case "Bo" -> multiplers.add(Integer.valueOf(m.group(2) == null ? "0" : m.group(2)));
                        case "Go" -> golds.add(Integer.valueOf(m.group(2)));
                        case "Sc" -> scores.add(Integer.valueOf(m.group(2)));
                    }
                }
            });
        }
        finally {
            System.setOut(stdout);
            System.setErr(stderr);
        }
        System.out.println("Total runs        : " + numRuns);
        System.out.println("Failed Explore    : " + (numRuns - multiplers.size()));
        System.out.println("Failed Escape     : " + failEscape.get());
        System.out.println();

        System.out.println("Average Multiplier: " + (multiplers.stream().reduce(Integer::sum).orElse(0) / numRuns));
        System.out.println("Average Gold      : " + (golds.stream().reduce(Integer::sum).orElse(0) / numRuns));
        System.out.println("Average Score     : " + (scores.stream().reduce(Integer::sum).orElse(0) / numRuns));

    }
}
