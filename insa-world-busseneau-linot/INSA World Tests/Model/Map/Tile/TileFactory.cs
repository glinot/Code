using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace INSAWorld.Tests
{
    [TestClass]
    public class TileFactoryTests
    {
        /// <summary>
        /// Test of the singleton.
        /// </summary>
        [TestMethod]
        public void TileFactoryTest()
        {
            Assert.AreSame(TileFactory.Instance, TileFactory.Instance);
        }

        /// <summary>
        /// Test of the flyweight with the desert tile.
        /// </summary>
        [TestMethod]
        public void TileFactoryDesertTest()
        {
            var instance = TileFactory.Instance;
            Assert.AreSame(instance.Desert, instance.Desert);
        }

        /// <summary>
        /// Test of the flyweight with the plain tile.
        /// </summary>
        [TestMethod]
        public void TileFactoryPlainTest()
        {
            var instance = TileFactory.Instance;
            Assert.AreSame(instance.Plain, instance.Plain);
        }

        /// <summary>
        /// Test of the flyweight with the swamp tile.
        /// </summary>
        [TestMethod]
        public void TileFactorySwampTest()
        {
            var instance = TileFactory.Instance;
            Assert.AreSame(instance.Swamp, instance.Swamp);
        }

        /// <summary>
        /// Test of the flyweight with the volcano tile.
        /// </summary>
        [TestMethod]
        public void TestFlightWeightVolcano()
        {
            var instance = TileFactory.Instance;
            Assert.AreSame(instance.Volcano, instance.Volcano);
        }
    }
}
