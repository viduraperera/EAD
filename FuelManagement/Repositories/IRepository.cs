namespace FuelManagement.Repositories
{
    public interface IRepository<T>
    {
        Task<T> GetByIdAsync(Guid Id);
        Task<IEnumerable<T>> GetAllAsync();
        Task CreateAsync(T data);
        Task UpdateAsync(T data);
    }
}