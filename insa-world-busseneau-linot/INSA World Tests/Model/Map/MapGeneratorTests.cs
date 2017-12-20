using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;

namespace INSAWorld.Tests
{
    [TestClass]
    public class MapGeneratorTests
    {
        /// <summary>
        /// Normal case.
        /// </summary>
        [TestMethod]
        public void GenerateMapTest()
        {
            var sizeMap = 12;
            Assert.AreEqual(new MapGenerator().GenerateMap(sizeMap).Size, sizeMap);
        }

        /// <summary>
        /// Out of the range of the size of the map.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void GenerateMapTestErrorSize()
        {
           new MapGenerator().GenerateMap(1);
        }

        private bool CheckDistribution(Map map)
        {
            var minTileNumber = map.Size * map.Size / 4;
            IDictionary<ITile, int> countMap = new Dictionary<ITile, int>();
            for (var i = 0; i < map.Size; i++)
                for (var j = 0; j < map.Size; j++)
                {
                    ITile tile = map.GetTile(new Position(i, j));
                    if (countMap.ContainsKey(tile))
                        countMap[tile]++;
                    else
                        countMap[tile] = 1;
                }

            foreach (var number in countMap.Values)
                if (number < minTileNumber || number > minTileNumber + 1)
                    return false;

            return true;
        }

        /// <summary>
        /// Test wheter the random strategy gives a random map.
        /// </summary>
        [TestMethod]
        public void GenerateMapRandomTest()
        {
            var mapGenerator = new MapGenerator().SetGenerator(MapGenerator.StrategyType.Random);

            for (var i = 2; i < 21; i++)
                Assert.IsTrue(CheckDistribution(mapGenerator.GenerateMap(i)));

            // Checks that 2 generated maps are different.
            var sizeMap = new Random().Next(2, 11);
            Map map1 = mapGenerator.GenerateMap(sizeMap);
            Map map2 = mapGenerator.GenerateMap(sizeMap);

            var same = true;
            for (var i = 0; i < sizeMap && same; i++)
                for (var j = 0; j < sizeMap && same; j++)
                    same = map1.GetTile(new Position(i, j)) == map2.GetTile(new Position(i, j));
            Assert.IsFalse(same);
        }
    }
}