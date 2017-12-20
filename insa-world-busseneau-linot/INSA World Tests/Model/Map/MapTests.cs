using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;

namespace INSAWorld.Tests
{
    [TestClass]
    public class MapTests
    {
        /// <summary>
        /// Normal case.
        /// Size and GetTile() are also test in this test.
        /// </summary>
        [TestMethod]
        public void MapTest()
        {
            var size = 5;
            var grid = new ITile[size, size];
            var tileFactory = TileFactory.Instance;

            for (var i = 0; i < size; i++)
                for (var j = 0; j < size; j++)
                    grid[i, j] = tileFactory.Desert;

            var map = new Map(grid);

            for (var i = 0; i < size; i++)
                for (var j = 0; j < size; j++)
                    Assert.AreSame(map.GetTile(new Position(i, j)), tileFactory.Desert);
            Assert.AreEqual(map.Size, size);
        }

        /// <summary>
        /// Grid is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void MapTestGridNull()
        {
            new Map(null);
        }

        /// <summary>
        /// Grid is too small.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void MapTestGridTooSmall()
        {
            var grid = new ITile[1, 1];
            grid[0, 0] = TileFactory.Instance.Desert;
            new Map(grid);
        }

        /// <summary>
        /// One of the grid element is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void MapTestGridElementNull()
        {
            var size = 2;
            var grid = new ITile[size, size];
            grid[0, 1] = grid[1, 0] = grid[1, 1] = TileFactory.Instance.Desert;

            new Map(grid);
        }

        /// <summary>
        /// Position is null.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void GetTileTestPositionNull()
        {
            new SmallMap().BuildMap().GetTile(null);
        }

        /// <summary>
        /// Position is invalid.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void GetTileTestOutOfRangeXPosition()
        {
            Map map = new SmallMap().BuildMap();
            map.GetTile(new Position(map.Size, 0));
        }

        /// <summary>
        /// Position is invalid.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void GetTileTestOutOfRangeYPosition()
        {
            Map map = new SmallMap().BuildMap();
            map.GetTile(new Position(0, map.Size));
        }
    }
}