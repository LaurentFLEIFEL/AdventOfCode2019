package com.lf.advent.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMap;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FuelProcessing implements LinesConsumer {
    private Map<String, Reaction> reactionByResult = Maps.mutable.empty();
    @Getter
    private long ore;

    @Setter
    private long neededFuel = 1;


    @Override
    public void consume(List<String> lines) {

        lines.stream()
             .map(this::convertToReaction)
             .forEach(reaction -> reactionByResult.put(reaction.getResult(), reaction));

        log.info("Reactions = \n{}", reactionByResult.values()
                                                     .stream()
                                                     .map(Reaction::toString)
                                                     .collect(Collectors.joining("\n")));

        execute();
        log.info("ORE = {}", ore);
    }

    public void execute() {
        Map<String, Long> components = reactionByResult.get("FUEL").needResult(neededFuel);

        while (components.size() != 1 || !components.containsKey("ORE")) {
            Map<String, Long> aux = Maps.mutable.empty();
            Map<String, Long> finalComponents = components;
            components.forEach((reactant, quantity) -> {
                if ("ORE".equals(reactant)) {
                    aux.merge("ORE", quantity, Long::sum);
                    return;
                }
                Map<String, Long> innerReactants = reactionByResult.get(reactant).needResult(quantity);

                if (shouldNotDecomposed(reactant, finalComponents.keySet())) {
                    aux.merge(reactant, quantity, Long::sum);
                    return;
                }

                innerReactants.forEach((innerReactant, innerQuantity) -> aux.merge(innerReactant, innerQuantity, Long::sum));
            });

            components = aux;
        }

        ore = components.get("ORE");
    }

    public boolean shouldNotDecomposed(String reactant, Set<String> components) {
        return components.stream()
                         .filter(component -> !component.equals(reactant))
                         .anyMatch(component -> canBeBuildFrom(component, reactant));
    }

    private boolean canBeBuildFrom(String component, String reactantToBeFound) {
        Set<String> components = reactionByResult.get(component).getReactant().keySet();

        while (components.size() != 1 || !components.contains("ORE")) {
            Set<String> aux = Sets.mutable.empty();
            for (String reactant : components) {
                if ("ORE".equals(reactant)) {
                    aux.add("ORE");
                    continue;
                }

                if (reactantToBeFound.equals(reactant)) {
                    return true;
                }
                Set<String> innerReactants = reactionByResult.get(reactant).getReactant().keySet();

                aux.addAll(innerReactants);
            }

            components = aux;
        }
        return false;
    }

    private Reaction convertToReaction(String line) {
        String[] split = line.split(" => ");
        String results = split[1].trim();
        String reactants = split[0].trim();

        String result = results.split(" ")[1];
        long resultQuantity = Long.parseLong(results.split(" ")[0]);

        MutableMap<String, Long> reactant = Maps.mutable.empty();
        Arrays.stream(reactants.split(", "))
              .map(s -> s.split(" "))
              .forEach(s -> reactant.put(s[1], Long.parseLong(s[0])));

        return Reaction.builder()
                       .result(result)
                       .resultQuantity(resultQuantity)
                       .reactant(reactant)
                       .build();
    }

    @Builder
    @ToString
    @Getter
    public static class Reaction {
        private String result;
        private long resultQuantity;
        private Map<String, Long> reactant;

        public Map<String, Long> needResult(long quantity) {
            MutableMap<String, Long> result = Maps.mutable.empty();
            if (resultQuantity > quantity) {
                reactant.forEach(result::put);
                return result;
            }

            long ratio = quantity / resultQuantity + (quantity % resultQuantity == 0 ? 0 : 1);

            reactant.forEach((key, value) -> result.put(key, value * ratio));
            return result;
        }
    }
}
