package com.lf.advent.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.factory.Lists;
import org.junit.Test;

import java.util.List;

@Slf4j
public class FuelProcessingTest {

    @Test
    public void test1() {
        List<String> lines = Lists.mutable.of("10 ORE => 10 A",
                                              "1 ORE => 1 B",
                                              "7 A, 1 B => 1 C",
                                              "7 A, 1 C => 1 D",
                                              "7 A, 1 D => 1 E",
                                              "7 A, 1 E => 1 FUEL");

        FuelProcessing service = new FuelProcessing();

        service.consume(lines);

        Assertions.assertThat(service.getOre()).isEqualTo(31);
    }

    @Test
    public void test2() {
        List<String> lines = Lists.mutable.of("9 ORE => 2 A",
                                              "8 ORE => 3 B",
                                              "7 ORE => 5 C",
                                              "3 A, 4 B => 1 AB",
                                              "5 B, 7 C => 1 BC",
                                              "4 C, 1 A => 1 CA",
                                              "2 AB, 3 BC, 4 CA => 1 FUEL");

        FuelProcessing service = new FuelProcessing();

        service.consume(lines);
        Assertions.assertThat(service.getOre()).isEqualTo(165);
    }

    @Test
    public void test3() {
        List<String> lines = Lists.mutable.of("157 ORE => 5 NZVS",
                                              "165 ORE => 6 DCFZ",
                                              "44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL",
                                              "12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ",
                                              "179 ORE => 7 PSHF",
                                              "177 ORE => 5 HKGWZ",
                                              "7 DCFZ, 7 PSHF => 2 XJWVT",
                                              "165 ORE => 2 GPVTF",
                                              "3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT");

        FuelProcessing service = new FuelProcessing();

        service.consume(lines);

        Assertions.assertThat(service.getOre()).isEqualTo(13312);
    }

    @Test
    public void test4() {
        List<String> lines = Lists.mutable.of("2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG",
                                              "17 NVRVD, 3 JNWZP => 8 VPVL",
                                              "53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL",
                                              "22 VJHF, 37 MNCFX => 5 FWMGM",
                                              "139 ORE => 4 NVRVD",
                                              "144 ORE => 7 JNWZP",
                                              "5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC",
                                              "5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV",
                                              "145 ORE => 6 MNCFX",
                                              "1 NVRVD => 8 CXFTF",
                                              "1 VJHF, 6 MNCFX => 4 RFSQX",
                                              "176 ORE => 6 VJHF");

        FuelProcessing service = new FuelProcessing();

        service.consume(lines);

        Assertions.assertThat(service.getOre()).isEqualTo(180697);
    }

    @Test
    public void test5() {
        List<String> lines = Lists.mutable.of("171 ORE => 8 CNZTR",
                                              "7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL",
                                              "114 ORE => 4 BHXH",
                                              "14 VRPVC => 6 BMBT",
                                              "6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL",
                                              "6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT",
                                              "15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW",
                                              "13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW",
                                              "5 BMBT => 4 WPTQ",
                                              "189 ORE => 9 KTJDG",
                                              "1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP",
                                              "12 VRPVC, 27 CNZTR => 2 XDBXC",
                                              "15 KTJDG, 12 BHXH => 5 XCVML",
                                              "3 BHXH, 2 VRPVC => 7 MZWV",
                                              "121 ORE => 7 VRPVC",
                                              "7 XCVML => 6 RJRHP",
                                              "5 BHXH, 4 VRPVC => 5 LTCX");

        FuelProcessing service = new FuelProcessing();

        service.consume(lines);

        Assertions.assertThat(service.getOre()).isEqualTo(2210736);
    }

    @Test
    public void test_part2_1() {
        long oreWanted = 1000000000000L;
        List<String> lines = Lists.mutable.of("157 ORE => 5 NZVS",
                                              "165 ORE => 6 DCFZ",
                                              "44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL",
                                              "12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ",
                                              "179 ORE => 7 PSHF",
                                              "177 ORE => 5 HKGWZ",
                                              "7 DCFZ, 7 PSHF => 2 XJWVT",
                                              "165 ORE => 2 GPVTF",
                                              "3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT");

        long fuel = computeFuel(oreWanted, lines);

        log.info("Fuel = {}", fuel);
        Assertions.assertThat(fuel).isEqualTo(82892753L);
    }

    @Test
    public void test_part2_2() {
        long oreWanted = 1000000000000L;
        List<String> lines = Lists.mutable.of("2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG",
                                              "17 NVRVD, 3 JNWZP => 8 VPVL",
                                              "53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL",
                                              "22 VJHF, 37 MNCFX => 5 FWMGM",
                                              "139 ORE => 4 NVRVD",
                                              "144 ORE => 7 JNWZP",
                                              "5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC",
                                              "5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV",
                                              "145 ORE => 6 MNCFX",
                                              "1 NVRVD => 8 CXFTF",
                                              "1 VJHF, 6 MNCFX => 4 RFSQX",
                                              "176 ORE => 6 VJHF");

        long fuel = computeFuel(oreWanted, lines);

        log.info("Fuel = {}", fuel);
        Assertions.assertThat(fuel).isEqualTo(5586022L);
    }

    @Test
    public void test_part2_3() {
        long oreWanted = 1000000000000L;
        List<String> lines = Lists.mutable.of("171 ORE => 8 CNZTR",
                                              "7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL",
                                              "114 ORE => 4 BHXH",
                                              "14 VRPVC => 6 BMBT",
                                              "6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL",
                                              "6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT",
                                              "15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW",
                                              "13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW",
                                              "5 BMBT => 4 WPTQ",
                                              "189 ORE => 9 KTJDG",
                                              "1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP",
                                              "12 VRPVC, 27 CNZTR => 2 XDBXC",
                                              "15 KTJDG, 12 BHXH => 5 XCVML",
                                              "3 BHXH, 2 VRPVC => 7 MZWV",
                                              "121 ORE => 7 VRPVC",
                                              "7 XCVML => 6 RJRHP",
                                              "5 BHXH, 4 VRPVC => 5 LTCX");

        long fuel = computeFuel(oreWanted, lines);

        log.info("Fuel = {}", fuel);
        Assertions.assertThat(fuel).isEqualTo(460664L);
    }

    @Test
    public void test_part2() {
        long oreWanted = 1000000000000L;
        List<String> lines = Lists.mutable.of("2 MLVWS, 8 LJNWK => 1 TNFQ",
                                              "1 BWXQJ => 2 BMWK",
                                              "1 JMGP, 3 WMJW => 9 JQCF",
                                              "8 BWXQJ, 10 BJWR => 6 QWSLS",
                                              "3 PLSH, 1 TNFQ => 6 CTPTW",
                                              "11 GQDJG, 5 BMWK, 1 FZCK => 7 RQCNC",
                                              "1 VWSRH => 7 PTGXM",
                                              "104 ORE => 7 VWSRH",
                                              "1 PTGXM, 13 WMJW, 1 BJGD => 7 KDHF",
                                              "12 QWSLS, 3 PLSH, 4 HFBPX, 2 DFTH, 11 BCTRK, 4 JPKWB, 4 MKMRC, 3 XQJZQ => 6 BDJK",
                                              "1 JQCF, 3 CVSC => 2 KRQHC",
                                              "128 ORE => 7 QLRXZ",
                                              "32 CXLWB, 18 TZWD => 1 HFQBG",
                                              "31 KDHF => 9 BWXQJ",
                                              "21 MLVWS => 9 LJNWK",
                                              "3 QLRXZ => 5 CXLWB",
                                              "3 LQWDR, 2 WSDH, 5 JPKWB, 1 RSTQC, 2 BJWR, 1 ZFNR, 16 QWSLS => 4 JTDT",
                                              "3 BWXQJ, 14 JMGP => 9 MSTS",
                                              "1 KXMKM, 2 LFCR => 9 DKWLT",
                                              "6 CVSC => 3 FWQVP",
                                              "6 XBVH, 1 HFBPX, 2 FZCK => 9 DFTH",
                                              "9 MSTS => 2 BCTRK",
                                              "1 PLSH, 28 MSTS => 2 FDKZ",
                                              "10 XBVH, 5 BJWR, 2 FWQVP => 6 ZFNR",
                                              "2 CVSC => 6 XBVH",
                                              "1 BWXQJ, 2 KXMKM => 3 XQJZQ",
                                              "1 VWSRH, 1 TZWD => 4 WMJW",
                                              "14 CTPTW, 19 JMGP => 8 GRWK",
                                              "13 NLGS, 1 PTGXM, 3 HFQBG => 5 BLVK",
                                              "2 PTGXM => 7 NLGS",
                                              "123 ORE => 3 DLPZ",
                                              "2 ZNRPX, 35 DKWLT => 3 WSDH",
                                              "1 TZWD, 1 BLVK, 9 BWXQJ => 2 MKDQF",
                                              "2 DLPZ => 2 MLVWS",
                                              "8 MKDQF, 4 JQCF, 12 VLMQJ => 8 VKCL",
                                              "1 KRQHC => 7 BJWR",
                                              "1 GRWK, 2 FWQVP => 9 LFCR",
                                              "2 MSTS => 2 GQDJG",
                                              "132 ORE => 9 TZWD",
                                              "1 FWQVP => 8 RHKZW",
                                              "43 FDKZ, 11 BJWR, 63 RHKZW, 4 PJCZB, 1 BDJK, 13 RQCNC, 8 JTDT, 3 DKWLT, 13 JPKWB => 1 FUEL",
                                              "1 LFCR, 5 DFTH => 1 RSTQC",
                                              "10 GQDJG => 8 KPTF",
                                              "4 BWXQJ, 1 MKDQF => 7 JMGP",
                                              "10 FGNPM, 23 DFTH, 2 CXLWB, 6 KPTF, 3 DKWLT, 10 MKDQF, 1 MJSG, 6 RSTQC => 8 PJCZB",
                                              "8 VWSRH, 1 DLPZ => 7 BJGD",
                                              "2 BLVK => 9 HBKH",
                                              "16 LQWDR, 3 MSTS => 9 HFBPX",
                                              "1 TNFQ, 29 HFQBG, 4 BLVK => 2 KXMKM",
                                              "11 CVSC => 8 MJSG",
                                              "3 LFCR => 6 FGNPM",
                                              "11 HFQBG, 13 MKDQF => 1 FZCK",
                                              "11 BWXQJ, 1 QLRXZ, 1 TNFQ => 9 KBTWZ",
                                              "7 XQJZQ, 6 VKCL => 7 LQWDR",
                                              "1 LJNWK, 4 HBKH => 1 CVSC",
                                              "4 PLSH, 2 WSDH, 2 KPTF => 5 JPKWB",
                                              "1 KPTF => 8 MKMRC",
                                              "5 NLGS, 2 KDHF, 1 KBTWZ => 2 VLMQJ",
                                              "4 MLVWS, 1 WMJW, 8 LJNWK => 1 PLSH",
                                              "3 VKCL => 7 ZNRPX");

        long fuel = computeFuel(oreWanted, lines);

        log.info("Fuel = {}", fuel);
    }

    public long computeFuel(long oreWanted, List<String> lines) {
        FuelProcessing service = new FuelProcessing();

        service.consume(lines);

        long orePerFuel = service.getOre();

        long minFuel = oreWanted / orePerFuel;

        long maxFuel = 2 * minFuel;
        long fuel = 0L;
        long previousFuel = -1L;

        long oreNeeded;

        while (previousFuel != fuel) {
            previousFuel = fuel;
            fuel = (minFuel + maxFuel) / 2;
            service.setNeededFuel(fuel);
            service.execute();
            oreNeeded = service.getOre();

            if (oreNeeded < oreWanted) {
                minFuel = minFuel + (maxFuel - minFuel) / 2;
            }

            if (oreNeeded > oreWanted) {
                maxFuel = minFuel + (maxFuel - minFuel) / 2;
            }
        }
        return fuel;
    }
}