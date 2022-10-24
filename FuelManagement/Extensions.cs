using FuelManagement.Dtos;
using FuelManagement.Entities;

namespace FuelManagement
{
    public static class Extensions
    {
        public static CustomerDto AsDto(this Customer customer)
        {
            return new CustomerDto(customer.Id, customer.Name, customer.Email, customer.Password, customer.status, customer.VehicleType, customer.ArrivalTime, customer.DepartureTime, customer.FuelStationId);
        }
        public static OwnerDto AsDto(this Owner owner)
        {
            return new OwnerDto(owner.Id, owner.Name, owner.Email, owner.Password, owner.status, owner.FuelType, owner.ArrivalTime, owner.FinishTime);
        }
    }
}