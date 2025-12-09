"use client";

import React from 'react';
import { useTable } from '@/contexts/TableContext';
import { MapPin, ChevronDown } from 'lucide-react';

interface TableSelectorProps {
  dictionary: {
    selectTable?: string;
    table?: string;
    noTables?: string;
    loading?: string;
  };
}

export function TableSelector({ dictionary }: TableSelectorProps) {
  const { selectedTable, tables, isLoading, setSelectedTable } = useTable();

  if (isLoading) {
    return (
      <div className="flex items-center gap-2 px-3 py-2 bg-gray-100 rounded-lg text-sm text-gray-500">
        <MapPin className="w-4 h-4" />
        <span>{dictionary.loading || "Loading..."}</span>
      </div>
    );
  }

  if (tables.length === 0) {
    return (
      <div className="flex items-center gap-2 px-3 py-2 bg-red-50 rounded-lg text-sm text-red-600">
        <MapPin className="w-4 h-4" />
        <span>{dictionary.noTables || "No tables available"}</span>
      </div>
    );
  }

  return (
    <div className="relative">
      <label className="flex items-center gap-2 px-3 py-2 bg-orange-50 rounded-lg cursor-pointer hover:bg-orange-100 transition-colors">
        <MapPin className="w-4 h-4 text-orange-600" />
        <select
          value={selectedTable?.id || ''}
          onChange={(e) => {
            const table = tables.find(t => t.id === parseInt(e.target.value));
            setSelectedTable(table || null);
          }}
          className="bg-transparent text-sm font-medium text-orange-800 cursor-pointer focus:outline-none appearance-none pr-6"
        >
          <option value="" disabled>
            {dictionary.selectTable || "Select Table"}
          </option>
          {tables.map((table) => (
            <option key={table.id} value={table.id}>
              {dictionary.table || "Table"} {table.tableNumber}
              {table.capacity ? ` (${table.capacity} seats)` : ''}
            </option>
          ))}
        </select>
        <ChevronDown className="w-4 h-4 text-orange-600 absolute right-3" />
      </label>
    </div>
  );
}


