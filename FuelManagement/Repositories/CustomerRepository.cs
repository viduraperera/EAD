using FuelManagement.Entities;
using MongoDB.Bson;
using MongoDB.Driver;

namespace FuelManagement.Repositories
{
    public class CustomerRepository: IRepository<Customer>
    {
        private const string databaseName = "FuelManagement";
        private const string collectionName = "customers";
        private readonly IMongoCollection<Customer> customersCollection;
        private readonly FilterDefinitionBuilder<Customer> filterBuilder = Builders<Customer>.Filter;

        public CustomerRepository(IMongoClient mongoClient)
        {
            IMongoDatabase database = mongoClient.GetDatabase(databaseName);
            customersCollection = database.GetCollection<Customer>(collectionName);
        }
        public async Task<Customer> GetByIdAsync(Guid Id)
        {
            var filter = filterBuilder.Eq(customer => customer.Id, Id);
            return await customersCollection.Find(filter).SingleOrDefaultAsync();
        }
        public async Task<IEnumerable<Customer>> GetAllAsync()
        {
            return await customersCollection.Find(new BsonDocument()).ToListAsync();
        }
        public async Task CreateAsync(Customer customer)
        {
            await customersCollection.InsertOneAsync(customer);
        }
        public async Task UpdateAsync(Customer customer)
        {
            var filter = filterBuilder.Eq(existingCustomer => existingCustomer.Id, customer.Id);
            await customersCollection.ReplaceOneAsync(filter, customer);
        }
         public async Task<Customer> filterByEmail(string email)
        {
            var filter = filterBuilder.Eq(owner => owner.Email, email);
            return await customersCollection.Find(filter).SingleOrDefaultAsync();
        }
    }
}