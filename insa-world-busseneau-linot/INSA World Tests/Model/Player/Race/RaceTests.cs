using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace INSAWorld.Tests
{
    [TestClass]
    public class RaceTests
    {
        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void CentaurTest()
        {
            var race = new Centaur();

            Assert.AreEqual(race.VictoryPointsOnDesert, 2);
            Assert.AreEqual(race.VictoryPointsOnPlain, 3);
            Assert.AreEqual(race.VictoryPointsOnSwamp, 1);
            Assert.AreEqual(race.VictoryPointsOnVolcano, 0);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void CerberusTest()
        {
            var race = new Cerberus();

            Assert.AreEqual(race.VictoryPointsOnDesert, 1);
            Assert.AreEqual(race.VictoryPointsOnPlain, 0);
            Assert.AreEqual(race.VictoryPointsOnSwamp, 2);
            Assert.AreEqual(race.VictoryPointsOnVolcano, 3);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void CyclopsTest()
        {
            var race = new Cyclops();

            Assert.AreEqual(race.VictoryPointsOnDesert, 3);
            Assert.AreEqual(race.VictoryPointsOnPlain, 2);
            Assert.AreEqual(race.VictoryPointsOnSwamp, 1);
            Assert.AreEqual(race.VictoryPointsOnVolcano, 0);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void ProduceWarriorCentaurTest()
        {
            var warrior = new Centaur().ProduceWarrior(Position.ZERO);
            Assert.IsInstanceOfType(warrior, typeof(CentaurWarrior));
        }

        /// <summary>
        /// Position is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ProduceWarriorCentaurTestNullPosition()
        {
            new Centaur().ProduceWarrior(null);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void ProduceWarriorCerberusTest()
        {
            var warrior = new Cerberus().ProduceWarrior(Position.ZERO);
            Assert.IsInstanceOfType(warrior, typeof(CerberusWarrior));
        }

        /// <summary>
        /// Position is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ProduceWarriorCerberusTestNullPosition()
        {
            new Cerberus().ProduceWarrior(null);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void ProduceWarriorCyclopsTest()
        {
            var warrior = new Cyclops().ProduceWarrior(Position.ZERO);
            Assert.IsInstanceOfType(warrior, typeof(CyclopsWarrior));
        }

        /// <summary>
        /// Position is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ProduceWarriorCyclopsTestNullPosition()
        {
            new Cyclops().ProduceWarrior(null);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void ComputeVictoryPointsCentaurTest()
        {
            var grid = new ITile[2, 2];
            var tileFactory = TileFactory.Instance;
            grid[0, 0] = tileFactory.Desert;
            grid[0, 1] = tileFactory.Plain;
            grid[1, 0] = tileFactory.Swamp;
            grid[1, 1] = tileFactory.Volcano;
            var map = new Map(grid);
            var race = new Centaur();
            var warrior1 = race.ProduceWarrior(Position.ZERO);
            var warrior2 = race.ProduceWarrior(new Position(0, 1));
            var warrior3 = race.ProduceWarrior(new Position(1, 0));
            var warrior4 = race.ProduceWarrior(new Position(1, 1));

            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior1), 2);
            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior2), 3);
            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior3), 1);
            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior4), 0);
        }

        /// <summary>
        /// Map is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ComputeVictoryPointsCentaurTestNullMap()
        {
            new Centaur().ComputeVictoryPoints(null, new CentaurWarrior(Position.ZERO));
        }

        /// <summary>
        /// Map is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ComputeVictoryPointsCentaurTestNullUnit()
        {
            new Centaur().ComputeVictoryPoints(new DemoMap().BuildMap(), null);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void ComputeVictoryPointsCerberusTest()
        {
            var grid = new ITile[2, 2];
            var tileFactory = TileFactory.Instance;
            grid[0, 0] = tileFactory.Desert;
            grid[0, 1] = tileFactory.Plain;
            grid[1, 0] = tileFactory.Swamp;
            grid[1, 1] = tileFactory.Volcano;
            var map = new Map(grid);
            var race = new Cerberus();
            var warrior1 = race.ProduceWarrior(Position.ZERO);
            var warrior2 = race.ProduceWarrior(new Position(0, 1));
            var warrior3 = race.ProduceWarrior(new Position(1, 0));
            var warrior4 = race.ProduceWarrior(new Position(1, 1));

            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior1), 1);
            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior2), 0);
            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior3), 2);
            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior4), 3);
        }

        /// <summary>
        /// Map is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ComputeVictoryPointsCerberusTestNullMap()
        {
            new Cerberus().ComputeVictoryPoints(null, new CerberusWarrior(Position.ZERO));
        }

        /// <summary>
        /// Map is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ComputeVictoryPointsCerberusTestNullUnit()
        {
            new Cerberus().ComputeVictoryPoints(new DemoMap().BuildMap(), null);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void ComputeVictoryPointsCyclopsTest()
        {
            var grid = new ITile[2, 2];
            var tileFactory = TileFactory.Instance;
            grid[0, 0] = tileFactory.Desert;
            grid[0, 1] = tileFactory.Plain;
            grid[1, 0] = tileFactory.Swamp;
            grid[1, 1] = tileFactory.Volcano;
            var map = new Map(grid);
            var race = new Cyclops();
            var warrior1 = race.ProduceWarrior(Position.ZERO);
            var warrior2 = race.ProduceWarrior(new Position(0, 1));
            var warrior3 = race.ProduceWarrior(new Position(1, 0));
            var warrior4 = race.ProduceWarrior(new Position(1, 1));

            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior1), 3);
            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior2), 2);
            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior3), 1);
            Assert.AreEqual(race.ComputeVictoryPoints(map, warrior4), 0);
        }

        /// <summary>
        /// Map is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ComputeVictoryPointsCyclopsTestNullMap()
        {
            new Cyclops().ComputeVictoryPoints(null, new CyclopsWarrior(Position.ZERO));
        }

        /// <summary>
        /// Map is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ComputeVictoryPointsCyclopsTestNullUnit()
        {
            new Cyclops().ComputeVictoryPoints(new DemoMap().BuildMap(), null);
        }
    }
}