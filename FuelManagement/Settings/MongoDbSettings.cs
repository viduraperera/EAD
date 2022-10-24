namespace FuelManagement.Settings
{
    public class MongoDbSettings
    {
        public string ConnectionString
        {
            get
            {
                return "mongodb://localhost:27017";
            }
        }
    }
}