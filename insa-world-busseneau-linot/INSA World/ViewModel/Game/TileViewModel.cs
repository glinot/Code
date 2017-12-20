using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace INSAWorld.ViewModel
{
    public class TileViewModel : ViewModelBase
    {
        private ITile tile;

        public ObservableCollection<UnitViewModel> Units { get; }

        public bool HasUnit
        {
            get
            {
                return Units.Count != 0;
            }
        }

        public string Texture
        {
            get
            {
                return $"/Resources/{tile.GetType().Name}.png";
            }
        }
        public bool IsReachable { get; set; }
        public bool IsBestMove { get; set; }

        public TileViewModel(ITile tile)
        {
            this.tile = tile;
            Units = new ObservableCollection<UnitViewModel>();
        }
    }
}