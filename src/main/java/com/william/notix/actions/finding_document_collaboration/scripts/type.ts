import type { PostgresJsDatabase } from "drizzle-orm/postgres-js";

export interface ServerArgument {
    dbHost: string;
    dbPort: number;
    dbName: string;
    dbUser: string;
    dbPass: string;
    appPort: number;
    appName: string;
}

export type DatabaseConnection = PostgresJsDatabase