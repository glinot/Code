using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace INSAWorld.Tests
{
    [TestClass]
    public class UnitTests
    {
        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void CentaurWarriorTest()
        {
            var unit = new CentaurWarrior(Position.ZERO);
            Assert.AreEqual(unit.InitialHealthPoints, 10);
            Assert.AreEqual(unit.AttackPoints, 8);
            Assert.AreEqual(unit.DefensePoints, 2);
            Assert.AreEqual(unit.Position, Position.ZERO);
            Assert.AreEqual(unit.HealthPoints, unit.InitialHealthPoints);
            Assert.AreEqual(unit.MovePoints, Unit.MAX_MOVE_POINTS);
        }

        /// <summary>
        /// Position is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void CentaurWarriorTestNullPosition()
        {
            new CentaurWarrior(null);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void CerberusWarriorTest()
        {
            var unit = new CerberusWarrior(Position.ZERO);
            Assert.AreEqual(unit.InitialHealthPoints, 10);
            Assert.AreEqual(unit.AttackPoints, 6);
            Assert.AreEqual(unit.DefensePoints, 4);
            Assert.AreEqual(unit.Position, Position.ZERO);
            Assert.AreEqual(unit.HealthPoints, unit.InitialHealthPoints);
            Assert.AreEqual(unit.MovePoints, Unit.MAX_MOVE_POINTS);
        }

        /// <summary>
        /// Position is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void CerberusWarriorTestNullPosition()
        {
            new CerberusWarrior(null);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void CyclopsWarriorTest()
        {
            var unit = new CyclopsWarrior(Position.ZERO);
            Assert.AreEqual(unit.InitialHealthPoints, 12);
            Assert.AreEqual(unit.AttackPoints, 4);
            Assert.AreEqual(unit.DefensePoints, 6);
            Assert.AreEqual(unit.Position, Position.ZERO);
            Assert.AreEqual(unit.HealthPoints, unit.InitialHealthPoints);
            Assert.AreEqual(unit.MovePoints, Unit.MAX_MOVE_POINTS);
        }

        /// <summary>
        /// Position is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void CyclopsWarriorTestNullPosition()
        {
            new CyclopsWarrior(null);
        }

        /// <summary>
        /// Health points are negative.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void UnitHealthPointsTestNegativeValue()
        {
            new CentaurWarrior(Position.ZERO).HealthPoints = -1;
        }

        /// <summary>
        /// Health points are higher than the maximum.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void UnitHealthPointsTestMaximumValue()
        {
            var unit = new CentaurWarrior(Position.ZERO);
            unit.HealthPoints = unit.InitialHealthPoints + 1;
        }

        /// <summary>
        /// Move points are negative.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void UnitMovePointsTestNegativeValue()
        {
            new CentaurWarrior(Position.ZERO).MovePoints = -1;
        }

        /// <summary>
        /// Move points are higher than the maximum.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void UnitMovePointsTestMaximumValue()
        {
            var unit = new CentaurWarrior(Position.ZERO);
            unit.MovePoints = Unit.MAX_MOVE_POINTS + 1;
        }

        /// <summary>
        /// Position is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void UnitPositionTestNullPosition()
        {
            var unit = new CentaurWarrior(Position.ZERO);
            unit.Position = null;
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void ResetMoveTest()
        {
            var unit = new CentaurWarrior(Position.ZERO);
            unit.MovePoints = 0;
            unit.ResetMove();
            Assert.AreEqual(unit.MovePoints, Unit.MAX_MOVE_POINTS);

            unit.HealthPoints = 0;
            unit.MovePoints = 0;
            unit.ResetMove();
            Assert.AreEqual(unit.MovePoints, 0);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void RecoverHealthTest()
        {
            var unit = new CentaurWarrior(Position.ZERO);
            unit.HealthPoints = unit.InitialHealthPoints - 1;
            unit.RecoverHealth();
            Assert.AreEqual(unit.HealthPoints, unit.InitialHealthPoints);

            unit.HealthPoints = 0;
            unit.RecoverHealth();
            Assert.AreEqual(unit.HealthPoints, 0);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void IsDeadTest()
        {
            var unit = new CentaurWarrior(Position.ZERO);
            Assert.IsFalse(unit.IsDead());
            unit.HealthPoints = 0;
            Assert.IsTrue(unit.IsDead());
        }

        
        [TestMethod]
        public void GetBestWayTest()
        {
            var map = new SmallMap().BuildMap();
            var race = new Cyclops();
            var unit = race.ProduceWarrior(Position.ZERO);
            var positions = unit.FindReachablePositions(map);

            var reacheablePositions = new List<Position>()
            {
                new Position(0,0),
                new Position(0,1),
                new Position(0,2),
                new Position(0,3),
                new Position(1,0),
                new Position(2,0),
                new Position(3,0),
                new Position(1,1),
                new Position(1,2),
                new Position(2,1)
            };
            
            foreach(var reachablePosition in reacheablePositions)
                if(!positions.Contains(reachablePosition))
                    Assert.Fail();
            Assert.AreEqual(positions.Count, reacheablePositions.Count);
        }
    }
}