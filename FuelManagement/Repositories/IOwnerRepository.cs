using FuelManagement.Dtos;
namespace FuelManagement.Repositories
{
    public interface IOwnerRepository<T>: IRepository<T>
    {
        Task<OwnerQueueDetails> getQueueCountById(Guid Id);
    }
}