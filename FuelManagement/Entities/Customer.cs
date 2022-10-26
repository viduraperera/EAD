namespace FuelManagement.Entities
{
    public class Customer: User
    {
        public string VehicleType { get; set; } = string.Empty;
        public DateTime ArrivalTime { get; set; }
        public DateTime DepartureTime { get; set; }

        public Guid FuelStationId { get; set; }
    }
}