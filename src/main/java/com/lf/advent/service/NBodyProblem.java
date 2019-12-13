package com.lf.advent.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.eclipse.collections.api.factory.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Getter
@Setter
public class NBodyProblem implements LinesConsumer {

    private long nbrOfSteps;
    private List<Body> bodies;
    private int totalEnergy;

    private List<Body> initialState = Lists.mutable.empty();

    @Override
    public void consume(List<String> lines) {

    }

    public void execute() {
        log.info("step #{}", 0);
        bodies.stream()
              .map(Body::toString)
              .forEach(System.out::println);

        bodies.forEach(body -> {
            initialState.add(Body.of(body.name + "", body.pos));
        });
        long count = 0;
        long xCycle = -1;
        long yCycle = -1;
        long zCycle = -1;

        boolean xHasBeenFound = false;
        boolean yHasBeenFound = false;
        boolean zHasBeenFound = false;

        for (long step = 0; step < nbrOfSteps; step++) {
            bodies.forEach(body -> body.applyGravity(bodies));
            bodies.forEach(Body::applyVelocity);

            count = step + 1;

            if (!xHasBeenFound && bodies.stream().allMatch(body -> initialState.stream().anyMatch(body::isEqualX))) {
                xCycle = count;
                xHasBeenFound = true;
            }

            if (!yHasBeenFound && bodies.stream().allMatch(body -> initialState.stream().anyMatch(body::isEqualY))) {
                yCycle = count;
                yHasBeenFound = true;
            }

            if (!zHasBeenFound && bodies.stream().allMatch(body -> initialState.stream().anyMatch(body::isEqualZ))) {
                zCycle = count;
                zHasBeenFound = true;
            }

            if (xCycle != -1 && yCycle != -1 && zCycle != -1) {
                break;
            }
        }

        log.info("step #{}", count);
        bodies.stream()
              .map(Body::toString)
              .forEach(System.out::println);

        totalEnergy = bodies.stream()
                            .mapToInt(Body::totalEnergy)
                            .sum();

        log.info("totalEnergy = {}", totalEnergy);

        long xyCycle = ArithmeticUtils.lcm(xCycle, yCycle);
        long cycle = ArithmeticUtils.lcm(xyCycle, zCycle);

        log.info("cycle = {}", cycle);
    }

    public static class Body {
        private String name;
        private Coordinates pos;
        @Setter
        private Coordinates vel = Coordinates.ZERO.toBuilder().build();

        public static Body of(String name, int x, int y, int z) {
            Body body = new Body();
            body.pos = Coordinates.of(x, y, z);
            body.name = name;
            return body;
        }

        public static Body of(String name, Coordinates pos) {
            Body body = new Body();
            body.pos = pos.toBuilder().build();
            body.name = name;
            return body;
        }

        public int potential() {
            return pos.energy();
        }

        public int kinetic() {
            return vel.energy();
        }

        public int totalEnergy() {
            return potential() * kinetic();
        }

        public void applyGravity(List<Body> bodies) {
            for (Body body : bodies) {
                if (body.equals(this)) {
                    continue;
                }
                Coordinates compare = this.pos.compare(body.pos);
                this.vel = this.vel.add(compare);
            }
        }

        public void applyVelocity() {
            this.pos = this.pos.add(this.vel);
        }

        public boolean isEqualTo(Body other) {
            return Objects.equals(name, other.name) &&
                   Objects.equals(pos, other.pos) &&
                   Objects.equals(vel, other.vel);
        }

        public boolean isEqualX(Body other) {
            return Objects.equals(name, other.name) &&
                   Objects.equals(pos.x, other.pos.x) &&
                   Objects.equals(vel.x, other.vel.x);
        }

        public boolean isEqualY(Body other) {
            return Objects.equals(name, other.name) &&
                   Objects.equals(pos.y, other.pos.y) &&
                   Objects.equals(vel.y, other.vel.y);
        }

        public boolean isEqualZ(Body other) {
            return Objects.equals(name, other.name) &&
                   Objects.equals(pos.z, other.pos.z) &&
                   Objects.equals(vel.z, other.vel.z);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Body body = (Body) o;
            return Objects.equals(name, body.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "{" +
                   "name='" + name + '\'' +
                   ", pos=" + pos +
                   ", vel=" + vel +
                   '}';
        }
    }

    @Builder(toBuilder = true)
    @EqualsAndHashCode
    public static class Coordinates {
        public static final Coordinates ZERO = Coordinates.of(0, 0, 0);

        private int x;
        private int y;
        private int z;

        public static Coordinates of(int x, int y, int z) {
            return Coordinates.builder()
                              .x(x)
                              .y(y)
                              .z(z)
                              .build();
        }

        public int energy() {
            return Math.abs(x) + Math.abs(y) + Math.abs(z);
        }

        @Override
        public String toString() {
            return String.format("<x=%3d, y=%3d, z=%3d>", x, y, z);
        }

        public Coordinates compare(Coordinates other) {
            return Coordinates.of(Integer.signum(-this.x + other.x), Integer.signum(-this.y + other.y), Integer.signum(-this.z + other.z));
        }

        public Coordinates add(Coordinates other) {
            return Coordinates.of(this.x + other.x, this.y + other.y, this.z + other.z);
        }
    }
}
