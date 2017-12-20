using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace INSAWorld.Tests
{
    [TestClass]
    public class AttackActionTests
    {
        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void AttackActionTest()
        {
            var map = new SmallMap().BuildMap();
            var attacker = new CentaurWarrior(Position.ZERO);
            var defender = new CerberusWarrior(new Position(1, 1));

            AttackAction action = new AttackAction(map, attacker, defender);
            Assert.AreEqual(action.Attacker, attacker);
            Assert.AreEqual(action.Defender, defender);
            Assert.AreEqual(action.InitialAttackerPosition, Position.ZERO);
        }

        /// <summary>
        /// Probabilist tests to measure the equiprobability.
        /// </summary>
        [TestMethod]
        public void AttackActionTestProbability()
        {
            var map = new SmallMap().BuildMap();

            var attackingUnitLoses = 0;
            var defendingUnitLoses = 0;
            for (var i = 0; i < 10000; i++)
            {
                var attackingUnit = new EqualPowerWarrior(Position.ZERO);
                var defendingUnit = new EqualPowerWarrior(new Position(1, 1));

                var action = new AttackAction(map, attackingUnit, defendingUnit);
                action.Do();
                if (action.Loser == attackingUnit)
                    attackingUnitLoses++;
                else
                    defendingUnitLoses++;
            }

            var attackerWin = attackingUnitLoses / (double) (attackingUnitLoses + defendingUnitLoses);
            var defenderWin = defendingUnitLoses / (double) (attackingUnitLoses + defendingUnitLoses);

            var epsilon = 0.01;
            Assert.IsTrue(attackerWin < 0.5 + epsilon && attackerWin > 0.5 - epsilon);
            Assert.IsTrue(defenderWin < 0.5 + epsilon && defenderWin > 0.5 - epsilon);
        }

        /// <summary>
        /// Map is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void AttackActionTestMapNull()
        {
            var attacker = new CentaurWarrior(Position.ZERO);
            var defender = new CerberusWarrior(new Position(1, 1));
            new AttackAction(null, attacker, defender);
        }

        /// <summary>
        /// Attacker is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(NullReferenceException))]
        public void AttackActionTestAttackerNull()
        {
            var map = new SmallMap().BuildMap();
            var defender = new CerberusWarrior(Position.ZERO);
            new AttackAction(map, null, defender);
        }

        /// <summary>
        /// Defender is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(NullReferenceException))]
        public void AttackActionTestDefenderNull()
        {
            var map = new SmallMap().BuildMap();
            var attacker = new CentaurWarrior(Position.ZERO);
            new AttackAction(map, attacker, null);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void DoTest()
        {
            var map = new SmallMap().BuildMap();
            var attacker = new CerberusWarrior(Position.ZERO);
            var defender = new CentaurWarrior(new Position(1, 1));
            AttackAction action = new AttackAction(map, attacker, defender);
            action.Do();
            var movePointsLost = 2;

            Assert.AreEqual(action.Loser.HealthPoints, action.Loser.InitialHealthPoints - action.HealthPointsLost);
            Assert.AreEqual(action.MovePointsLost, movePointsLost);
            Assert.AreEqual(attacker.MovePoints, Unit.MAX_MOVE_POINTS - movePointsLost);
        }

        /// <summary>
        /// Normal case.
        /// Moved unit should be back at the first position after undo.
        /// </summary>
        [TestMethod]
        public void UndoTest()
        {
            var map = new SmallMap().BuildMap();
            var attacker = new CerberusWarrior(Position.ZERO);
            var defender = new CentaurWarrior(new Position(1, 1));
            AttackAction action = new AttackAction(map, attacker, defender);
            action.Do();
            var movePointsLost = 2;
            action.Undo();

            Assert.AreEqual(action.MovePointsLost, movePointsLost);
            Assert.AreEqual(attacker.Position, Position.ZERO);
            Assert.AreEqual(action.Loser.InitialHealthPoints, action.Loser.HealthPoints);
            Assert.AreEqual(attacker.MovePoints, Unit.MAX_MOVE_POINTS);
        }


         class EqualPowerWarrior : Unit
        {
            /// <summary>
            /// 
            /// </summary>
            /// <param name="initialPosition"></param>
            public EqualPowerWarrior(Position initialPosition) : base(10, 10, 10, initialPosition) { }

            /// <summary>
            /// 
            /// </summary>
            /// <param name="map"></param>
            /// <param name="position"></param>
            /// <returns></returns>
            protected override double GetMovePoints(Map map, Position position)
            {
                if (map == null)
                    throw new ArgumentNullException("map");
                if (position == null)
                    throw new ArgumentNullException("position");

                return map.GetTile(position) is Plain ? 0.5 : 1;
            }
        }
    }
}