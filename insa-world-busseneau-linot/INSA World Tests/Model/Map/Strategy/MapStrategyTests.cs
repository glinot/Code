using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace INSAWorld.Tests
{
    [TestClass]
    public class MapStrategyTests
    {
        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void DemoMapSizeTest()
        {
            Assert.AreEqual(new DemoMap().BuildMap().Size, 6);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void SmallMapSizeTest()
        {
            Assert.AreEqual(new SmallMap().BuildMap().Size, 10);
        }

        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void StandardMapSizeTest()
        {
            Assert.AreEqual(new StandardMap().BuildMap().Size, 14);
        }
    }
}