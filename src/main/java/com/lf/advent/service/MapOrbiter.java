package com.lf.advent.service;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class MapOrbiter implements LinesConsumer {

    private Map<String, Astre> astreByName = Maps.mutable.empty();

    private Set<Astre> astres = Sets.mutable.empty();

    @Getter
    private int count;

    @Getter
    private int minTransfer;

    @Override
    public void consume(List<String> lines) {
        lines.stream()
             .map(line -> line.split("\\)"))
             .forEach(objects -> {
                 String parent = objects[0];
                 String child = objects[1];
                 Astre astreParent = astreByName.computeIfAbsent(parent, Astre::of);
                 Astre astreChild = astreByName.computeIfAbsent(child, name -> Astre.of(name, astreParent));
                 astreChild.setParent(astreParent);
                 astres.add(astreChild);
                 astres.add(astreParent);
             });

        count = astres.stream()
                      .mapToInt(Astre::numberOfOrbit)
                      .sum();
        log.info("Count = {}", count);

        if(!(astreByName.containsKey("YOU") && astreByName.containsKey("SAN"))) {
            return;
        }

        Astre you = astreByName.get("YOU");
        Astre san = astreByName.get("SAN");

        SetUtils.SetView<Astre> disjunction = SetUtils.disjunction(you.ascendance(), san.ascendance());
        minTransfer = disjunction.size();
        log.info("Transfers = {}", minTransfer);
    }

    @Data
    public static class Astre {
        private Astre parent;
        private String name;

        public static Astre of(String name) {
            Astre astre = new Astre();
            astre.name = name;
            return astre;
        }

        public static Astre of(String name, Astre parent) {
            Astre astre = new Astre();
            astre.name = name;
            astre.parent = parent;
            return astre;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Astre astre = (Astre) o;
            return Objects.equals(name, astre.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "Astre{" +
                   "parent=" + parent +
                   ", name='" + name + '\'' +
                   '}';
        }

        public int numberOfOrbit() {
            if (Objects.isNull(parent)) {
                return 0;
            }
            return parent.numberOfOrbit() + 1;
        }

        public Set<Astre> ascendance() {
            if (Objects.isNull(parent)) {
                return Sets.mutable.empty();
            }
            Set<Astre> ascendance = parent.ascendance();
            ascendance.add(parent);
            return ascendance;
        }
    }
}
