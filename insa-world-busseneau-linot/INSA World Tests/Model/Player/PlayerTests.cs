using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Linq;

namespace INSAWorld.Tests
{
    [TestClass]
    public class PlayerTests
    {
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void PlayerTestNullName()
        {
            new Player(null, new Centaur(), 10, Position.ZERO);
        }
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void PlayerTestNullRace()
        {
            new Player("test player", null, 10, Position.ZERO);
        }
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void PlayerTestNotEnoughtUnits()
        {
            new Player("Test Player", new Cerberus(), 0, Position.ZERO);
        }
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void PlayerTestNullInitialPosition()
        {
            new Player("test player", new Cyclops(), 10, null);
        }

        [TestMethod]
        public void TestIsDecimated()
        {
            Player p = new Player("Test", new Cerberus(), 2000, Position.ZERO);
            // kill all units 
            p.Units.AsParallel().ForAll(u => u.HealthPoints = 0);
            // Test player is Decimated
            Assert.IsTrue(p.IsDecimated());

        }

        [TestMethod]
        public void TestIsNotDecimated()
        {
            Player p = new Player("Test", new Cerberus(), 2000, Position.ZERO);
            // Test player is Decimated
            Assert.IsFalse(p.IsDecimated());

        }

        [TestMethod]
        public void TestNumberOfGeneratedUnits()
        {
            int NbUnits = 31459;
            Player p = new Player("Test", new Cerberus(), NbUnits, Position.ZERO);
            Assert.AreEqual(p.Units.Count, NbUnits);
        }
    }
}