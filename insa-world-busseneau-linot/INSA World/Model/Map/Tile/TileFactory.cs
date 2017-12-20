namespace INSAWorld
{
    /// <summary>
    /// Factory which constructs the tile of a map.
    /// This factory implements the flyweight pattern to avoid the useless tile instanciations.
    /// This class is a singleton.
    /// </summary>
    public class TileFactory
    {
        private static TileFactory instance;

        /// <summary>
        /// The unique instance of the factory.
        /// It is a lazy initialisation.
        /// </summary>
        public static TileFactory Instance
        {
            get
            {
                if (instance == null)
                    instance = new TileFactory();

                return instance;
            }
        }

        private Desert desert;

        /// <summary>
        /// The unique instance of the desert tile.
        /// It is a lazy initialisation.
        /// </summary>
        public Desert Desert
        {
            get
            {
                if (desert == null)
                    desert = new Desert();
                return desert;
            }
        }

        private Plain plain;

        /// <summary>
        /// The unique instance of the plain tile.
        /// It is a lazy initialisation.
        /// </summary>
        public Plain Plain
        {
            get
            {
                if (plain == null)
                    plain = new Plain();
                return plain;
            }
        }

        private Swamp swamp;

        /// <summary>
        /// The unique instance of the swamp tile.
        /// It is a lazy initialisation.
        /// </summary>
        public Swamp Swamp
        {
            get
            {
                if (swamp == null)
                    swamp = new Swamp();
                return swamp;
            }
        }

        private Volcano volcano;

        /// <summary>
        /// The unique instance of the volcano tile.
        /// It is a lazy initialisation.
        /// </summary>
        public Volcano Volcano
        {
            get
            {
                if (volcano == null)
                    volcano = new Volcano();
                return volcano;
            }
        }

        /// <summary>
        /// The constructor is private because it is a singleton.
        /// Please use the unique factory instance.
        /// </summary>
        private TileFactory() {}
    }
}