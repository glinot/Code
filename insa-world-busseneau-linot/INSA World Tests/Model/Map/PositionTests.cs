using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;

namespace INSAWorld.Tests
{
    [TestClass]
    public class PositionTests
    {
        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void PositionTest()
        {
            var x = 1;
            var y = 1;
            var position = new Position(x, y);

            Assert.AreEqual(position.X, x);
            Assert.AreEqual(position.Y, y);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void PositionZEROTest()
        {
            Assert.AreSame(Position.ZERO, Position.ZERO);
            Assert.AreEqual(Position.ZERO.X, 0);
            Assert.AreEqual(Position.ZERO.Y, 0);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void PositionEqualsTest()
        {
            var position1 = new Position(1, 1);
            var position2 = new Position(1, 1);

            Assert.IsTrue(position1.X == position2.X);
            Assert.IsTrue(position1.Y == position2.Y);
        }

        /// <summary>
        /// Position is invalid.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void PositionTestOutOfRangeX()
        {
            new Position(-1, 0);
        }

        /// <summary>
        /// Position is invalid.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void PositionTestOutOfRangeY()
        {
            new Position(0, -1);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void SquaredDistanceTest()
        {
            var position = new Position(1, 1);
            Assert.AreEqual(position.SquaredDistance(Position.ZERO), 2);
            Assert.AreEqual(Position.ZERO.SquaredDistance(position), 2);
        }

        /// <summary>
        /// Position is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void SquaredDistanceTestNullPosition()
        {
            Position.ZERO.SquaredDistance(null);
        }
    }
}