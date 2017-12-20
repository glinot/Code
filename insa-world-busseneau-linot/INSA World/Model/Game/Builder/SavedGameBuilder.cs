namespace INSAWorld
{
    /// <summary>
    /// 
    /// </summary>
    public class SavedGameBuilder : IGameBuilder
    {
        /// <summary>
        /// 
        /// </summary>
        public string Path { get; private set; }

        private SavedGameBuilder() {}

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public static SavedGameBuilder Create()
        {
            return new SavedGameBuilder();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public Game Build()
        {
            return Game.LoadGame(Path);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="path"></param>
        /// <returns></returns>
        public SavedGameBuilder SetPath(string path)
        {
            Path = path;

            return this;
        }
    }
}