// using FuelManagement.
using System.ComponentModel.DataAnnotations;
namespace FuelManagement.Dtos
{
    public record OwnerDto(Guid Id, string Name, string Email, string status, string FuelType, string Location, DateTime ArrivalTime, DateTime FinishTime);
    public record OwnerDtoWithPassword(Guid Id, string Name, string Email,  byte[] PasswordHash, byte[] PasswordSalt, string status, string FuelType, string Location, DateTime ArrivalTime, DateTime FinishTime);
    public record CreateOwnerDto([Required]string Name, [Required]string Email, string Password, [Required]string FuelType, [Required] string Location);
    public record UpdateArrivalTimeOwnerDto([Required]Guid FuelStationId);
    public record UpdateDepartureTimeOwnerDto([Required]bool DidPumpedFuel);
    public record OwnerQueueDetails(Guid _id, string Name, CustomerDtoWithPassword[] customers);
    public record QueueDto(Guid Id, TimeSpan estimatedTime, int queue);
    public record OwnerLogin([Required] string email, [Required] string password);
    public record LoggedInOwnerDto(OwnerDto owner, string token);
}