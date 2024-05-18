import { drizzle } from "drizzle-orm/postgres-js";
import postgres from "postgres";
import type { DatabaseConnection } from "./type";

export function createDatabaseConnection(
    param : 
    {   
        databaseHost: string;
        databasePort: number;
        databaseName: string;
        databaseUsername: string;
        databasePassword: string;
    }
): DatabaseConnection {
    return drizzle(
        postgres(
            {
                host: param.databaseHost,
                port: param.databasePort,
                database: param.databaseName,
                username: param.databaseUsername,
                password: param.databasePassword
            }
        )
    ); 
}
