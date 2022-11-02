using System.ComponentModel.DataAnnotations;
namespace FuelManagement.Dtos
{
    public record CustomerDto(Guid Id, string Name, string Email, string status, string VehicleType, DateTime ArrivalTime, DateTime DepartureTime, Guid FuelStationId);
    public record CustomerDtoWithPassword(Guid Id, string Name, string Email, byte [] PasswordHash, byte[] PasswordSalt, string status, string VehicleType, DateTime ArrivalTime, DateTime DepartureTime, Guid FuelStationId);
    public record CreateCustomerDto([Required]string Name, [Required]string Email, string Password, [Required]string VehicleType);
    public record UpdateArrivalTimeCustomerDto([Required]Guid FuelStationId);
    public record UpdateDepartureTimeCustomerDto([Required]bool DidPumpedFuel);
    public record LoggedInCustomerDto(CustomerDto user, string token);
}