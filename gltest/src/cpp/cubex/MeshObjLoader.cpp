/*
 * ObjLoader.cpp
 *
 *  Created on: 17.06.2013
 *      Author: il
 */

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <vector>

#include "MeshObjLoader.h"

using namespace std;

namespace cubex
{

	MeshObjLoader::MeshObjLoader()
	{
		// TODO Auto-generated constructor stub

	}

	string readLine(FILE* file)
	{
		char ch = 0;
		string res = "";
		bool eoln = false;
		while (!eoln)
		{
			ch = fgetc(file);
			if (ch == 13)
			{
				char nl2 = fgetc(file);
				if (nl2 != 10) ungetc(nl2, file);
				eoln = true;
			}
			else if (ch == 10)
			{
				eoln = true;
			}
			else if (feof(file))
			{
				eoln = true;
			}
			else
			{
				res += ch;
			}
		}

		return res;
	}

	vector<string> parseTokens(const string& line)
	{
		vector<string> res;
		if (line.length() == 0) return res;
		int index = 0;

		res.push_back("");

		// Skipping spaces on the start of the line
		while (index < line.length() && (line[index] == ' ' || line[index] == '\t'))
		{
			index++;
		}

		char current = 0;

		bool previousEmpty = false;
		while (index < line.length())
		{
			current = line[index];
			bool currentEmpty = (current == ' ' || current == '\t');
			if (!previousEmpty && !currentEmpty)
			{
				res[res.size() - 1] += current;
			}
			else if (!previousEmpty && currentEmpty)
			{
				// Do nothing, just relax
			}
			else if (previousEmpty && !currentEmpty)
			{
				res.push_back(string("") + current);
			}
			previousEmpty = currentEmpty;
			index++;
		}

		return res;
	}

	vector<string> parseSlashed(string slashed)
	{
		vector<string> res;
		res.push_back("");

		for (int i = 0; i < slashed.size(); i++)
		{
			if (slashed[i] != '/')
			{
				res[res.size() - 1] += slashed[i];
			}
			else
			{
				res.push_back("");
			}
		}

		return res;
	}

	Mesh MeshObjLoader::createMeshFromFile(const string& fileName)
	{
		FILE* f = fopen(fileName.c_str(), "r");
		Mesh res;

		bool error = false;
		string errorMessage;

		int lineNumber = 1;

		while (!feof(f))
		{
			string line = readLine(f);

			vector<string> tokens = parseTokens(line);

			if (tokens.size() > 0)
			{
				if (tokens[0] == "v")
				{
					// Vertex instruction

					if (tokens.size() != 4)
					{
						error = true;
						errorMessage = "Incorrect number of tokens in vertex declaration at line " + lineNumber;
						break;
					}

					float x = strtod(tokens[1].c_str(), NULL);
					float y = strtod(tokens[2].c_str(), NULL);
					float z = strtod(tokens[3].c_str(), NULL);

					res.addVertex(glm::vec3(x, y, z));
				}
				else if (tokens[0] == "vt")
				{
					// Texture coordinates instruction

					if (tokens.size() != 3)
					{
						error = true;
						errorMessage = "Incorrect number of tokens in vertex coordinates declaration at line " + lineNumber;
						break;
					}

					float u = strtod(tokens[1].c_str(), NULL);
					float v = strtod(tokens[2].c_str(), NULL);

					res.addTextureCoords(glm::vec2(u, v));
				}
				else if (tokens[0] == "vn")
				{
					// Normal instruction

					if (tokens.size() != 4)
					{
						error = true;
						errorMessage = "Incorrect number of tokens in normal declaration at line " + lineNumber;
						break;
					}

					float x = strtod(tokens[1].c_str(), NULL);
					float y = strtod(tokens[2].c_str(), NULL);
					float z = strtod(tokens[3].c_str(), NULL);

					res.addNormal(glm::vec3(x, y, z));
				}
				else if (tokens[0] == "f")
				{
					// Face instruction

					if (tokens.size() == 4)
					{
						// Triangular face
						Face3 face;

						vector<string> indices[3];

						indices[0] = parseSlashed(tokens[1].c_str());
						indices[1] = parseSlashed(tokens[2].c_str());
						indices[2] = parseSlashed(tokens[3].c_str());

						if (indices[0].size() == 1)
						{
							// No texture coords, no normals

							if (indices[1].size() == 1 && indices[2].size() == 1)
							{
								face.containsNormals = false;
								face.containsTextureCoords = false;

								int v1 = atoi(indices[0][0].c_str()) - 1;
								int v2 = atoi(indices[1][0].c_str()) - 1;
								int v3 = atoi(indices[2][0].c_str()) - 1;

								face = Face3::fromVertices(v1, v2, v3);
							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}
						else if (indices[0].size() == 2)
						{
							// Texture coords only

							if (indices[1].size() == 2 && indices[2].size() == 2)
							{
								int v1 = atoi(indices[0][0].c_str()) - 1;
								int v2 = atoi(indices[1][0].c_str()) - 1;
								int v3 = atoi(indices[2][0].c_str()) - 1;

								int vt1 = atoi(indices[0][1].c_str()) - 1;
								int vt2 = atoi(indices[1][1].c_str()) - 1;
								int vt3 = atoi(indices[2][1].c_str()) - 1;

								face = Face3::fromVerticesAndTextureCoords(v1, v2, v3, vt1, vt2, vt3);
							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}
						else if (indices[0].size() == 3)
						{
							if (indices[1].size() == 3 && indices[2].size() == 3)
							{
								if (indices[0][1] == "" && indices[1][1] == "" && indices[2][1] == "")
								{
									// Normals only (no texture coordinates)

									int v1 = atoi(indices[0][0].c_str()) - 1;
									int v2 = atoi(indices[1][0].c_str()) - 1;
									int v3 = atoi(indices[2][0].c_str()) - 1;

									int vn1 = atoi(indices[0][2].c_str()) - 1;
									int vn2 = atoi(indices[1][2].c_str()) - 1;
									int vn3 = atoi(indices[2][2].c_str()) - 1;

									face = Face3::fromVerticesAndNormals(v1, v2, v3, vn1, vn2, vn3);
								}
								else if (indices[0][1] != "" && indices[1][1] != "" && indices[2][1] != "")
								{
									// Normals and texture coordinates

									int v1 = atoi(indices[0][0].c_str()) - 1;
									int v2 = atoi(indices[1][0].c_str()) - 1;
									int v3 = atoi(indices[2][0].c_str()) - 1;

									int vt1 = atoi(indices[0][1].c_str()) - 1;
									int vt2 = atoi(indices[1][1].c_str()) - 1;
									int vt3 = atoi(indices[2][1].c_str()) - 1;

									int vn1 = atoi(indices[0][2].c_str()) - 1;
									int vn2 = atoi(indices[1][2].c_str()) - 1;
									int vn3 = atoi(indices[2][2].c_str()) - 1;

									face = Face3::fromVerticesAndTextureCoordsAndNormals(v1, v2, v3, vt1, vt2, vt3, vn1, vn2, vn3);
								}
								else
								{
									error = true;
									errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
									break;
								}

							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}

						res.addFace3(face);

					}
					else if (tokens.size() == 5)
					{
						// Quad face

						Face4 face;

						vector<string> indices[4];
						indices[0] = parseSlashed(tokens[1].c_str());
						indices[1] = parseSlashed(tokens[2].c_str());
						indices[2] = parseSlashed(tokens[3].c_str());
						indices[3] = parseSlashed(tokens[4].c_str());

						if (indices[0].size() == 1)
						{
							// No texture coords, no normals

							if (indices[1].size() == 1 && indices[2].size() == 1 && indices[3].size() == 1)
							{
								face.containsNormals = false;
								face.containsTextureCoords = false;

								int v1 = atoi(indices[0][0].c_str()) - 1;
								int v2 = atoi(indices[1][0].c_str()) - 1;
								int v3 = atoi(indices[2][0].c_str()) - 1;
								int v4 = atoi(indices[3][0].c_str()) - 1;

								face = Face4::fromVertices(v1, v2, v3, v4);
							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}
						else if (indices[0].size() == 2)
						{
							// Texture coords only

							if (indices[1].size() == 2 && indices[2].size() == 2 && indices[3].size() == 2)
							{
								int v1 = atoi(indices[0][0].c_str()) - 1;
								int v2 = atoi(indices[1][0].c_str()) - 1;
								int v3 = atoi(indices[2][0].c_str()) - 1;
								int v4 = atoi(indices[3][0].c_str()) - 1;

								int vt1 = atoi(indices[0][1].c_str()) - 1;
								int vt2 = atoi(indices[1][1].c_str()) - 1;
								int vt3 = atoi(indices[2][1].c_str()) - 1;
								int vt4 = atoi(indices[3][1].c_str()) - 1;

								face = Face4::fromVerticesAndTextureCoords(v1, v2, v3, v4, vt1, vt2, vt3, vt4);
							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}
						else if (indices[0].size() == 3)
						{
							if (indices[1].size() == 3 && indices[2].size() == 3 && indices[3].size() == 3)
							{
								if (indices[0][1] == "" && indices[1][1] == "" && indices[2][1] == "" && indices[3][1] == "")
								{
									// Normals only (no texture coordinates)

									int v1 = atoi(indices[0][0].c_str()) - 1;
									int v2 = atoi(indices[1][0].c_str()) - 1;
									int v3 = atoi(indices[2][0].c_str()) - 1;
									int v4 = atoi(indices[3][0].c_str()) - 1;

									int vn1 = atoi(indices[0][2].c_str()) - 1;
									int vn2 = atoi(indices[1][2].c_str()) - 1;
									int vn3 = atoi(indices[2][2].c_str()) - 1;
									int vn4 = atoi(indices[3][2].c_str()) - 1;

									face = Face4::fromVerticesAndNormals(v1, v2, v3, v4, vn1, vn2, vn3, vn4);
								}
								else if (indices[0][1] != "" && indices[1][1] != "" && indices[2][1] != "")
								{
									// Normals and texture coordinates

									int v1 = atoi(indices[0][0].c_str()) - 1;
									int v2 = atoi(indices[1][0].c_str()) - 1;
									int v3 = atoi(indices[2][0].c_str()) - 1;
									int v4 = atoi(indices[3][0].c_str()) - 1;

									int vt1 = atoi(indices[0][1].c_str()) - 1;
									int vt2 = atoi(indices[1][1].c_str()) - 1;
									int vt3 = atoi(indices[2][1].c_str()) - 1;
									int vt4 = atoi(indices[3][1].c_str()) - 1;

									int vn1 = atoi(indices[0][2].c_str()) - 1;
									int vn2 = atoi(indices[1][2].c_str()) - 1;
									int vn3 = atoi(indices[2][2].c_str()) - 1;
									int vn4 = atoi(indices[3][2].c_str()) - 1;


									face = Face4::fromVerticesAndTextureCoordsAndNormals(v1, v2, v3, v4, vt1, vt2, vt3, vt4, vn1, vn2, vn3, vn4);
								}
								else
								{
									error = true;
									errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
									break;
								}

							}
							else
							{
								error = true;
								errorMessage = "Incorrect number of indices in face declaration at line" + lineNumber;
								break;
							}
						}

						res.addFace4(face);
					}
					else
					{
						error = true;
						errorMessage = "Incorrect number of tokens in face declaration at line " + lineNumber;
						break;
					}
				}
				else
				{
					// Unrecognized line
				}
			}

			lineNumber ++;
		}

		fclose(f);

		if (error)
		{
			throw CubexException(errorMessage);
		}

		return res;
	}

	MeshObjLoader::~MeshObjLoader()
	{
		// TODO Auto-generated destructor stub
	}

} /* namespace cubex */
