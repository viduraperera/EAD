using FuelManagement.Entities;
using MongoDB.Bson;
using MongoDB.Driver;
using FuelManagement.Dtos;

namespace FuelManagement.Repositories
{
    public class OwnerRepository: IOwnerRepository<Owner>
    {
        private const string databaseName = "FuelManagement";
        private const string collectionName = "owners";
        private readonly IMongoCollection<Owner> ownersCollection;
        private readonly FilterDefinitionBuilder<Owner> filterBuilder = Builders<Owner>.Filter;

        public OwnerRepository(IMongoClient mongoClient)
        {
            IMongoDatabase database = mongoClient.GetDatabase(databaseName);
            ownersCollection = database.GetCollection<Owner>(collectionName);
        }
        public async Task<Owner> GetByIdAsync(Guid Id)
        {
            var filter = filterBuilder.Eq(owner => owner.Id, Id);
            return await ownersCollection.Find(filter).SingleOrDefaultAsync();
        }
        public async Task<IEnumerable<Owner>> GetAllAsync()
        {
            return await ownersCollection.Find(new BsonDocument()).ToListAsync();
        }
        public async Task CreateAsync(Owner owner)
        {
            await ownersCollection.InsertOneAsync(owner);
        }
        public async Task UpdateAsync(Owner owner)
        {
            var filter = filterBuilder.Eq(existingOwner => existingOwner.Id, owner.Id);
            await ownersCollection.ReplaceOneAsync(filter, owner);
        }

        public async Task<OwnerQueueDetails> getQueueCountById(Guid id)
        {
            BsonDocument pipelineStage1 = new BsonDocument{
                {
                    "$match", new BsonDocument{
                        { "_id", id.ToString() }
                    }
                }
            };
            BsonDocument pipelineStage2 = new BsonDocument{
                {
                    "$lookup", new BsonDocument{
                        { "from", "customers" },
                        { "localField", "_id" },
                        { "foreignField", "FuelStationId" },
                        { "as", "customers" }
                    }
                }
            };
            BsonDocument pipelineStage3 = new BsonDocument{
                { "$unwind", "$customers" }
            };

            BsonDocument pipelineStage4 = new BsonDocument{
                {
                    "$match", new BsonDocument{
                        {"$or", new BsonArray{
                            new BsonDocument{
                                {"customers.status", "In Queue"}
                            },
                            new BsonDocument{
                                {"customers.status", "Fuel Pumped"}
                            }
                        }}
                    }
                }
            };
            BsonDocument pipelineStage5 = new BsonDocument{
                {
                    "$group", new BsonDocument{
                        { "_id", "$_id" },
                        { 
                            "Name", new BsonDocument{
                                { "$first", "$Name" }
                            } 
                        },
                        { 
                            "customers", new BsonDocument{
                                { "$addToSet", "$customers" }
                            }
                        }
                    }
                }
            };
            BsonDocument[] pipeline = new BsonDocument[] { 
                pipelineStage1,
                pipelineStage2,
                pipelineStage3,
                pipelineStage4,
                pipelineStage5,
            };
            
            OwnerQueueDetails queue = (await ownersCollection.AggregateAsync<OwnerQueueDetails>(pipeline)).SingleOrDefault();
            return queue;
        }
    }
}