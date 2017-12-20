using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace INSAWorld.Tests
{
    [TestClass]
    public class MoveActionTests
    {
        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void MoveActionTest()
        {
            var map = new SmallMap().BuildMap();
            var unit = new CentaurWarrior(Position.ZERO);
            var position = new Position(1, 1);
            MoveAction action = new MoveAction(map, unit, position);

            Assert.AreEqual(action.Unit, unit);
            Assert.AreEqual(action.Position, position);
        }

        /// <summary>
        /// Map is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void MoveActionTestMapNull()
        {
            var unit = new CentaurWarrior(Position.ZERO);
            var position = new Position(1, 1);
            new MoveAction(null, unit, position);
        }

        /// <summary>
        /// Unit is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(NullReferenceException))]
        public void MoveActionTestUnitNull()
        {
            var map = new SmallMap().BuildMap();
            var position = new Position(1, 1);
            new MoveAction(map, null, position);
        }

        /// <summary>
        /// Position is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void MoveActionTestPositionNull()
        {
            var map = new SmallMap().BuildMap();
            var unit = new CentaurWarrior(Position.ZERO);
            new MoveAction(map, unit, null);
        }

        /// <summary>
        /// Out of the range of the cost of the action.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void MoveActionTestErrorCost()
        {
            var map = new SmallMap().BuildMap();
            var unit = new CerberusWarrior(Position.ZERO);
            var position = new Position(map.Size, map.Size);
            new MoveAction(map, unit, position);
        }

        /// <summary>
        /// Normal case.
        /// Moved unit should be at the designed position.
        /// </summary>
        [TestMethod]
        public void DoTest()
        {
            var map = new SmallMap().BuildMap();
            var unit = new CerberusWarrior(Position.ZERO);
            var position = new Position(1, 1);
            MoveAction action = new MoveAction(map, unit, position);
            action.Do();
            var movePointsLost = 2;

            Assert.AreEqual(unit.Position, position);
            Assert.AreEqual(action.MovePointsLost, movePointsLost);
            Assert.AreEqual(unit.MovePoints, Unit.MAX_MOVE_POINTS - movePointsLost);
        }

        /// <summary>
        /// Normal case.
        /// Moved unit should be back at the first position after undo.
        /// </summary>
        [TestMethod]
        public void UndoTest()
        {
            var map = new SmallMap().BuildMap();
            var unit = new CerberusWarrior(Position.ZERO);
            var position = new Position(1, 1);
            MoveAction action = new MoveAction(map, unit, position);
            action.Do();
            var movePointsLost = 2;
            action.Undo();

            Assert.AreEqual(action.MovePointsLost, movePointsLost);
            Assert.AreEqual(unit.Position, Position.ZERO);
            Assert.AreEqual(unit.MovePoints, Unit.MAX_MOVE_POINTS);
        }
    }
}